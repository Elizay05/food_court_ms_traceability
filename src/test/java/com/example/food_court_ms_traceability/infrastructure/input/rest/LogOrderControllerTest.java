package com.example.food_court_ms_traceability.infrastructure.input.rest;

import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.application.dto.response.EmployeeEfficiencyResponse;
import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.application.dto.response.OrderEfficiencyResponse;
import com.example.food_court_ms_traceability.application.handler.ILogOrderHandler;
import com.example.food_court_ms_traceability.infrastructure.util.RoleConstants;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogOrderControllerTest {

    @Mock
    private ILogOrderHandler logOrderHandler;

    @InjectMocks
    private LogOrderController controller;

    @Test
    public void test_customer_can_register_valid_order_status_change() {
        // Arrange
        LogOrderRequest request = new LogOrderRequest("order123", "customer456", "rest123", "chef789", "PENDING");

        // Act & Assert
        assertDoesNotThrow(() -> controller.registerChangeState(request));

        verify(logOrderHandler).registerChangeState(request);
    }

    @Test
    public void test_get_history_order_success() {
        // Arrange
        String pedidoId = "order123";
        List<LogOrderResponse> expectedResponse = Arrays.asList(
                new LogOrderResponse("PENDING", LocalDateTime.now()),
                new LogOrderResponse("COMPLETED", LocalDateTime.now())
        );

        when(logOrderHandler.getHistoryOrder(pedidoId)).thenReturn(expectedResponse);

        Authentication auth = new UsernamePasswordAuthenticationToken("customer", null,
                Collections.singletonList(new SimpleGrantedAuthority(RoleConstants.CUSTOMER)));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        List<LogOrderResponse> result = controller.getHistoryOrder(pedidoId);

        // Assert
        assertEquals(expectedResponse, result);
        verify(logOrderHandler).getHistoryOrder(pedidoId);
    }

    @Test
    public void test_owner_can_get_order_efficiency() {
        // Arrange
        String pedidoId = "123";
        OrderEfficiencyResponse expectedResponse = new OrderEfficiencyResponse(pedidoId, 10L, 30L);

        when(logOrderHandler.getOrderEfficiency(pedidoId)).thenReturn(expectedResponse);

        // Act
        OrderEfficiencyResponse actualResponse = controller.getOrderEfficiency(pedidoId);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(logOrderHandler).getOrderEfficiency(pedidoId);
    }

    @Test
    public void test_owner_can_retrieve_employee_ranking() {
        List<EmployeeEfficiencyResponse> expectedResponse = Arrays.asList(
                new EmployeeEfficiencyResponse("chef1", 10L, 30L),
                new EmployeeEfficiencyResponse("chef2", 15L, 45L)
        );
        when(logOrderHandler.getEmployeeEfficiencyRanking()).thenReturn(expectedResponse);

        // Act
        List<EmployeeEfficiencyResponse> actualResponse = controller.getEmployeeEfficiencyRanking();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(logOrderHandler).getEmployeeEfficiencyRanking();
    }

    @Test
    public void test_delete_order_logs_success() {
        // Arrange
        String orderId = "123";

        // Act
        ResponseEntity<Void> response = controller.deleteOrderLogs(orderId);

        // Assert
        verify(logOrderHandler).deleteOrderLogs(orderId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
