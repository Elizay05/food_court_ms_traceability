package com.example.food_court_ms_traceability.application.handler.impl;

import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.application.mapper.ILogOrderRequestMapper;
import com.example.food_court_ms_traceability.domain.api.ILogOrderServicePort;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.infrastructure.configuration.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogOrderHandlerTest {

    @Mock
    private ILogOrderServicePort logOrderServicePort;

    @Mock
    private ILogOrderRequestMapper logOrderRequestMapper;

    @InjectMocks
    private LogOrderHandler logOrderHandler;

    @Test
    public void test_valid_log_order_request_maps_to_domain() {
        // Arrange
        LogOrderRequest request = new LogOrderRequest("order123", "client456", "PENDING");
        LogOrder expectedLogOrder = new LogOrder("order123", "client456", "PENDING", LocalDateTime.now());

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
}
