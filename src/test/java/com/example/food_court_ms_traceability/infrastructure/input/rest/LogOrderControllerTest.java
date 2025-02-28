package com.example.food_court_ms_traceability.infrastructure.input.rest;

import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.application.handler.ILogOrderHandler;
import com.example.food_court_ms_traceability.infrastructure.util.RoleConstants;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        LogOrderRequest request = new LogOrderRequest("order123", "customer456", "PENDING");

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
}
