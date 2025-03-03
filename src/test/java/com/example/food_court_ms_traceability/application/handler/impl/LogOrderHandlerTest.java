package com.example.food_court_ms_traceability.application.handler.impl;

import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.application.dto.response.EmployeeEfficiencyResponse;
import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.application.dto.response.OrderEfficiencyResponse;
import com.example.food_court_ms_traceability.application.mapper.IEmployeeEfficiencyResponseMapper;
import com.example.food_court_ms_traceability.application.mapper.ILogOrderRequestMapper;
import com.example.food_court_ms_traceability.application.mapper.IOrderEfficiencyResponseMapper;
import com.example.food_court_ms_traceability.domain.api.ILogOrderServicePort;
import com.example.food_court_ms_traceability.domain.model.EmployeeEfficiency;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.domain.model.OrderEfficiency;
import com.example.food_court_ms_traceability.infrastructure.configuration.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogOrderHandlerTest {

    @Mock
    private ILogOrderServicePort logOrderServicePort;

    @Mock
    private ILogOrderRequestMapper logOrderRequestMapper;

    @Mock
    private IOrderEfficiencyResponseMapper orderEfficiencyResponseMapper;

    @Mock
    private IEmployeeEfficiencyResponseMapper employeeEfficiencyResponseMapper;

    @InjectMocks
    private LogOrderHandler logOrderHandler;

    @Test
    public void test_valid_log_order_request_maps_to_domain() {
        // Arrange
        LogOrderRequest request = new LogOrderRequest("order123", "client456", "rest123", "chef789", "PENDING");
        LogOrder expectedLogOrder = new LogOrder("order123", "client456", "rest123", "chef789", "PENDING", LocalDateTime.now());

        when(logOrderRequestMapper.toDomain(request)).thenReturn(expectedLogOrder);

        // Act
        logOrderHandler.registerChangeState(request);

        // Assert
        verify(logOrderRequestMapper).toDomain(request);
        verify(logOrderServicePort).registerChangeState(expectedLogOrder);
    }

    @Test
    public void test_get_history_order_success() {
        String pedidoId = "123";
        String documentNumber = "1234567890";
        List<LogOrderResponse> expectedResponse = Arrays.asList(new LogOrderResponse("PENDING", LocalDateTime.now()));

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getDocumentNumber()).thenReturn(documentNumber);
        SecurityContextHolder.setContext(securityContext);

        when(logOrderServicePort.getHistoryOrder(pedidoId, documentNumber)).thenReturn(expectedResponse);

        List<LogOrderResponse> result = logOrderHandler.getHistoryOrder(pedidoId);

        assertEquals(expectedResponse, result);
        verify(logOrderServicePort).getHistoryOrder(pedidoId, documentNumber);
    }

    @Test
    public void test_get_order_efficiency_success() {
        // Arrange
        String pedidoId = "123";
        String nit = "456";

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        OrderEfficiency orderEfficiency = new OrderEfficiency(pedidoId, 10, 30);
        OrderEfficiencyResponse expectedResponse = new OrderEfficiencyResponse(pedidoId, 10, 30);

        when(userDetails.getNit()).thenReturn(nit);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(logOrderServicePort.getOrderEfficiency(pedidoId, nit)).thenReturn(orderEfficiency);
        when(orderEfficiencyResponseMapper.toResponse(orderEfficiency)).thenReturn(expectedResponse);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // Act
            OrderEfficiencyResponse result = logOrderHandler.getOrderEfficiency(pedidoId);

            // Assert
            assertEquals(expectedResponse, result);
            verify(logOrderServicePort).getOrderEfficiency(pedidoId, nit);
            verify(orderEfficiencyResponseMapper).toResponse(orderEfficiency);
        }
    }

    @Test
    public void test_get_employee_efficiency_ranking_success() {
        // Arrange
        String nit = "123456789";
        CustomUserDetails userDetails = new CustomUserDetails("user", "doc123", nit, Collections.emptyList());
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<EmployeeEfficiency> mockEfficiencies = Arrays.asList(
                new EmployeeEfficiency("chef1", 10, 30),
                new EmployeeEfficiency("chef2", 15, 45)
        );

        when(logOrderServicePort.getEmployeeEfficiencyRanking(nit)).thenReturn(mockEfficiencies);
        when(employeeEfficiencyResponseMapper.toResponse(mockEfficiencies))
                .thenReturn(Arrays.asList(
                        new EmployeeEfficiencyResponse("chef1", 10, 30),
                        new EmployeeEfficiencyResponse("chef2", 15, 45)
                ));


        // Act
        List<EmployeeEfficiencyResponse> result = logOrderHandler.getEmployeeEfficiencyRanking();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(logOrderServicePort).getEmployeeEfficiencyRanking(nit);
        verify(employeeEfficiencyResponseMapper).toResponse(mockEfficiencies);
    }

    @Test
    public void test_delete_order_logs_success() {
        // Arrange
        String pedidoId = "123";
        String documentNumber = "1234567890";

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getDocumentNumber()).thenReturn(documentNumber);

        // Act
        logOrderHandler.deleteOrderLogs(pedidoId);

        // Assert
        verify(logOrderServicePort).deleteOrderLogs(pedidoId, documentNumber);
    }
}
