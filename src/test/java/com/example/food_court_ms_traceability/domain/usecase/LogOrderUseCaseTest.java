package com.example.food_court_ms_traceability.domain.usecase;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.exception.OrderLogsNotFoundException;
import com.example.food_court_ms_traceability.domain.exception.UnauthorizedAccessException;
import com.example.food_court_ms_traceability.domain.model.EmployeeEfficiency;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.domain.model.OrderEfficiency;
import com.example.food_court_ms_traceability.domain.spi.ILogOrderPersistencePort;
import com.example.food_court_ms_traceability.domain.util.ExceptionConstants;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogOrderUseCaseTest {

    @Mock
    private ILogOrderPersistencePort logOrderPersistencePort;

    @InjectMocks
    private LogOrderUseCase logOrderUseCase;

    @Test
    public void test_register_change_state_success() {
        LogOrder logOrder = new LogOrder("order123", "client123", "rest123", "chef123", "PENDING", null);

        // Act
        logOrderUseCase.registerChangeState(logOrder);

        // Assert
        verify(logOrderPersistencePort).saveLog(logOrder);
        assertNotNull(logOrder.getFechaCambio());
    }

    @Test
    public void test_get_history_order_success() {
        // Arrange
        String pedidoId = "123";
        String clienteId = "456";

        List<LogOrder> logs = Arrays.asList(
                new LogOrder(pedidoId, clienteId, "789", "012", "PENDING", LocalDateTime.now())
        );

        List<LogOrderResponse> expectedResponse = Arrays.asList(
                new LogOrderResponse("PENDING", LocalDateTime.now())
        );

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(true);
        when(logOrderPersistencePort.getLogsByPedidoId(pedidoId)).thenReturn(logs);
        when(logOrderPersistencePort.getHistoryOrder(pedidoId)).thenReturn(expectedResponse);

        // Act
        List<LogOrderResponse> result = logOrderUseCase.getHistoryOrder(pedidoId, clienteId);

        // Assert
        assertEquals(expectedResponse, result);
        verify(logOrderPersistencePort).existsByPedidoId(pedidoId);
        verify(logOrderPersistencePort).getLogsByPedidoId(pedidoId);
        verify(logOrderPersistencePort).getHistoryOrder(pedidoId);
    }

    @Test
    public void test_get_history_order_not_found() {
        // Arrange
        String pedidoId = "123";
        String clienteId = "456";

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(false);

        // Act & Assert
        OrderLogsNotFoundException exception = assertThrows(
                OrderLogsNotFoundException.class,
                () -> logOrderUseCase.getHistoryOrder(pedidoId, clienteId)
        );

        assertEquals(ExceptionConstants.HISTORY_ORDER_NOT_FOUND, exception.getMessage());
        verify(logOrderPersistencePort).existsByPedidoId(pedidoId);
        verify(logOrderPersistencePort, never()).getLogsByPedidoId(any());
        verify(logOrderPersistencePort, never()).getHistoryOrder(any());
    }

    @Test
    public void test_unauthorized_access_exception_when_cliente_id_not_associated() {
        // Arrange
        String pedidoId = "123";
        String clienteId = "456";
        String differentClienteId = "789";

        List<LogOrder> logs = Arrays.asList(
                new LogOrder(pedidoId, differentClienteId, "789", "012", "PENDING", LocalDateTime.now())
        );

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(true);
        when(logOrderPersistencePort.getLogsByPedidoId(pedidoId)).thenReturn(logs);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> {
            logOrderUseCase.getHistoryOrder(pedidoId, clienteId);
        });

        verify(logOrderPersistencePort).existsByPedidoId(pedidoId);
        verify(logOrderPersistencePort).getLogsByPedidoId(pedidoId);
    }

    @Test
    public void test_get_order_efficiency_success() {
        // Arrange
        String pedidoId = "order123";
        String restauranteId = "rest123";

        LogOrder log1 = new LogOrder(pedidoId, "client1", restauranteId, "chef1", "PENDING",
                LocalDateTime.of(2023, 1, 1, 10, 0));
        LogOrder log2 = new LogOrder(pedidoId, "client1", restauranteId, "chef1", "COMPLETED",
                LocalDateTime.of(2023, 1, 1, 10, 5));

        List<LogOrder> logs = Arrays.asList(log1, log2);

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(true);
        when(logOrderPersistencePort.getOrderLogs(pedidoId)).thenReturn(logs);

        // Act
        OrderEfficiency result = logOrderUseCase.getOrderEfficiency(pedidoId, restauranteId);

        // Assert
        assertEquals(5, result.getMinutes());
        assertEquals(0, result.getSeconds());
        assertEquals(pedidoId, result.getPedidoId());
    }

    @Test
    public void test_get_order_efficiency_order_not_found() {
        // Arrange
        String pedidoId = "nonexistent";
        String restauranteId = "rest123";

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(false);

        // Act & Assert
        OrderLogsNotFoundException exception = assertThrows(OrderLogsNotFoundException.class, () -> {
            logOrderUseCase.getOrderEfficiency(pedidoId, restauranteId);
        });

        assertEquals(ExceptionConstants.HISTORY_ORDER_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void test_unauthorized_access_exception_when_restaurante_id_does_not_match() {
        // Arrange
        String pedidoId = "order123";
        String restauranteId = "rest123";
        String differentRestauranteId = "rest456";

        LogOrder log1 = new LogOrder(pedidoId, "client1", restauranteId, "chef1", "PENDING",
                LocalDateTime.of(2023, 1, 1, 10, 0));
        LogOrder log2 = new LogOrder(pedidoId, "client1", differentRestauranteId, "chef1", "COMPLETED",
                LocalDateTime.of(2023, 1, 1, 10, 5));

        List<LogOrder> logs = Arrays.asList(log1, log2);

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(true);
        when(logOrderPersistencePort.getOrderLogs(pedidoId)).thenReturn(logs);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> {
            logOrderUseCase.getOrderEfficiency(pedidoId, restauranteId);
        });
    }

    @Test
    public void test_returns_sorted_employee_efficiency_list() {
        String restaurantId = "rest123";
        List<LogOrder> logs = Arrays.asList(
                new LogOrder("1", "c1", restaurantId, "chef1", "PENDING", LocalDateTime.now()),
                new LogOrder("1", "c1", restaurantId, "chef1", "READY", LocalDateTime.now().plusMinutes(5)),
                new LogOrder("2", "c2", restaurantId, "chef2", "PENDING", LocalDateTime.now()),
                new LogOrder("2", "c2", restaurantId, "chef2", "READY", LocalDateTime.now().plusMinutes(3))
        );

        when(logOrderPersistencePort.getAllOrderLogs(restaurantId)).thenReturn(logs);

        List<EmployeeEfficiency> result = logOrderUseCase.getEmployeeEfficiencyRanking(restaurantId);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getMinutes() <= result.get(1).getMinutes());
    }

    @Test
    public void test_empty_logs_returns_empty_list() {
        String restaurantId = "rest123";
        when(logOrderPersistencePort.getAllOrderLogs(restaurantId)).thenReturn(Collections.emptyList());

        List<EmployeeEfficiency> result = logOrderUseCase.getEmployeeEfficiencyRanking(restaurantId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void test_all_logs_with_null_or_empty_chefId() {
        String restaurantId = "rest123";
        List<LogOrder> logs = Arrays.asList(
                new LogOrder("1", "c1", restaurantId, null, "PENDING", LocalDateTime.now()),
                new LogOrder("2", "c2", restaurantId, "", "READY", LocalDateTime.now().plusMinutes(5))
        );

        when(logOrderPersistencePort.getAllOrderLogs(restaurantId)).thenReturn(logs);

        List<EmployeeEfficiency> result = logOrderUseCase.getEmployeeEfficiencyRanking(restaurantId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void test_single_log_entry_per_chef() {
        String restaurantId = "rest123";
        List<LogOrder> logs = Arrays.asList(
                new LogOrder("1", "c1", restaurantId, "chef1", "PENDING", LocalDateTime.now())
        );

        when(logOrderPersistencePort.getAllOrderLogs(restaurantId)).thenReturn(logs);

        List<EmployeeEfficiency> result = logOrderUseCase.getEmployeeEfficiencyRanking(restaurantId);

        assertEquals(1, result.size());
        assertEquals("chef1", result.get(0).getChefId());
        assertEquals(0, result.get(0).getMinutes());
        assertEquals(0, result.get(0).getSeconds());
    }

    @Test
    public void test_delete_logs_success_when_order_exists_and_client_associated() {
        // Arrange
        String pedidoId = "123";
        String clienteId = "456";

        LogOrder logOrder = new LogOrder(pedidoId, clienteId, "789", "012", "PENDING", LocalDateTime.now());
        List<LogOrder> logs = Collections.singletonList(logOrder);

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(true);
        when(logOrderPersistencePort.getLogsByPedidoId(pedidoId)).thenReturn(logs);

        // Act
        logOrderUseCase.deleteOrderLogs(pedidoId, clienteId);

        // Assert
        verify(logOrderPersistencePort).deleteLogsByPedidoId(pedidoId);
    }

    @Test
    public void test_delete_logs_throws_exception_when_order_not_exists() {
        // Arrange
        String pedidoId = "123";
        String clienteId = "456";

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(false);

        // Act & Assert
        OrderLogsNotFoundException exception = assertThrows(OrderLogsNotFoundException.class, () -> {
            logOrderUseCase.deleteOrderLogs(pedidoId, clienteId);
        });

        assertEquals(ExceptionConstants.HISTORY_ORDER_NOT_FOUND, exception.getMessage());
        verify(logOrderPersistencePort, never()).deleteLogsByPedidoId(any());
    }

    @Test
    public void test_delete_logs_unauthorized_client() {
        // Arrange
        String pedidoId = "123";
        String unauthorizedClienteId = "999";

        LogOrder logOrder = new LogOrder(pedidoId, "456", "789", "012", "PENDING", LocalDateTime.now());
        List<LogOrder> logs = Collections.singletonList(logOrder);

        when(logOrderPersistencePort.existsByPedidoId(pedidoId)).thenReturn(true);
        when(logOrderPersistencePort.getLogsByPedidoId(pedidoId)).thenReturn(logs);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> {
            logOrderUseCase.deleteOrderLogs(pedidoId, unauthorizedClienteId);
        });
    }
}
