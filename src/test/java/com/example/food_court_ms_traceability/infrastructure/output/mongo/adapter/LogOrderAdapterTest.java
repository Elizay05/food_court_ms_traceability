package com.example.food_court_ms_traceability.infrastructure.output.mongo.adapter;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.infrastructure.exception.OrderHistoryNotFoundException;
import com.example.food_court_ms_traceability.infrastructure.exception.UnauthorizedAccessException;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.entity.LogOrderEntity;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.mapper.ILogOrderEntityMapper;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.repository.ILogOrderRepository;
import com.example.food_court_ms_traceability.infrastructure.util.ErrorMessages;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LogOrderAdapterTest {

    @Mock
    private ILogOrderRepository logOrderRepository;

    @Mock
    private ILogOrderEntityMapper logOrderEntityMapper;

    @InjectMocks
    private LogOrderAdapter logOrderAdapter;

    @Test
    public void test_save_valid_log_order() {
        // Arrange
        LogOrder logOrder = new LogOrder("123", "456", "PENDING", LocalDateTime.now());
        LogOrderEntity logOrderEntity = new LogOrderEntity();

        when(logOrderEntityMapper.toEntity(logOrder)).thenReturn(logOrderEntity);

        // Act
        logOrderAdapter.saveLog(logOrder);

        // Assert
        verify(logOrderEntityMapper).toEntity(logOrder);
        verify(logOrderRepository).save(logOrderEntity);
    }

    @Test
    public void test_get_history_order_success() {
        // Arrange
        String pedidoId = "order123";
        String clienteId = "client123";
        LocalDateTime now = LocalDateTime.now();

        LogOrderEntity log = new LogOrderEntity();
        log.setClienteId(clienteId);
        log.setPedidoId(pedidoId);
        log.setEstado("PENDING");
        log.setFechaCambio(now);

        List<LogOrderEntity> mockLogs = Collections.singletonList(log);

        when(logOrderRepository.findByPedidoId(pedidoId)).thenReturn(mockLogs);

        // Act
        List<LogOrderResponse> result = logOrderAdapter.getHistoryOrder(pedidoId, clienteId);

        // Assert
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getEstado());
        assertEquals(now, result.get(0).getFechaCambio());
        verify(logOrderRepository).findByPedidoId(pedidoId);
    }

    @Test
    public void test_get_history_order_not_found() {
        // Arrange
        String pedidoId = "order123";
        String clienteId = "client123";

        when(logOrderRepository.findByPedidoId(pedidoId)).thenReturn(Collections.emptyList());


        // Act & Assert
        OrderHistoryNotFoundException exception = assertThrows(
                OrderHistoryNotFoundException.class,
                () -> logOrderAdapter.getHistoryOrder(pedidoId, clienteId)
        );

        assertEquals(ErrorMessages.HISTORY_ORDER_NOT_FOUND, exception.getMessage());
        verify(logOrderRepository).findByPedidoId(pedidoId);
    }

    @Test
    public void test_unauthorized_access_exception_thrown_when_cliente_id_does_not_match() {
        // Arrange
        String pedidoId = "order123";
        String clienteId = "client123";
        String differentClienteId = "client456";
        LocalDateTime now = LocalDateTime.now();

        LogOrderEntity log1 = new LogOrderEntity();
        log1.setClienteId(clienteId);
        log1.setPedidoId(pedidoId);
        log1.setEstado("PENDING");
        log1.setFechaCambio(now);

        LogOrderEntity log2 = new LogOrderEntity();
        log2.setClienteId(differentClienteId);
        log2.setPedidoId(pedidoId);
        log2.setEstado("SHIPPED");
        log2.setFechaCambio(now.plusDays(1));

        List<LogOrderEntity> mockLogs = Arrays.asList(log1, log2);

        when(logOrderRepository.findByPedidoId(pedidoId)).thenReturn(mockLogs);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> {
            logOrderAdapter.getHistoryOrder(pedidoId, clienteId);
        });

        verify(logOrderRepository).findByPedidoId(pedidoId);
    }
}
