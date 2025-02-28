package com.example.food_court_ms_traceability.domain.usecase;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.domain.spi.ILogOrderPersistencePort;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LogOrderUseCaseTest {

    @Mock
    private ILogOrderPersistencePort logOrderPersistencePort;

    @InjectMocks
    private LogOrderUseCase logOrderUseCase;

    @Test
    public void test_register_change_state_updates_fecha_cambio() {
        // Arrange
        LogOrder logOrder = new LogOrder("123", "456", "PENDING", null);
        LocalDateTime beforeTest = LocalDateTime.now();

        // Act
        logOrderUseCase.registerChangeState(logOrder);

        // Assert
        assertNotNull(logOrder.getFechaCambio());
        assertTrue(logOrder.getFechaCambio().isAfter(beforeTest) ||
                logOrder.getFechaCambio().isEqual(beforeTest));
        verify(logOrderPersistencePort).saveLog(logOrder);
    }

    @Test
    public void test_get_history_order_returns_list() {
        // Arrange
        String pedidoId = "123";
        String clienteId = "456";
        List<LogOrderResponse> expectedResponse = Arrays.asList(
                new LogOrderResponse("PENDING", LocalDateTime.now()),
                new LogOrderResponse("COMPLETED", LocalDateTime.now())
        );

        when(logOrderPersistencePort.getHistoryOrder(pedidoId, clienteId)).thenReturn(expectedResponse);

        LogOrderUseCase logOrderUseCase = new LogOrderUseCase(logOrderPersistencePort);

        // Act
        List<LogOrderResponse> result = logOrderUseCase.getHistoryOrder(pedidoId, clienteId);

        // Assert
        assertEquals(expectedResponse, result);
        verify(logOrderPersistencePort).getHistoryOrder(pedidoId, clienteId);
    }
}
