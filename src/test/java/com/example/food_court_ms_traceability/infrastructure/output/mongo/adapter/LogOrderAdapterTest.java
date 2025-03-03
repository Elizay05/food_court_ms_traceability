package com.example.food_court_ms_traceability.infrastructure.output.mongo.adapter;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.entity.LogOrderEntity;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.mapper.ILogOrderEntityMapper;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.repository.ILogOrderRepository;
import com.example.food_court_ms_traceability.infrastructure.util.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LogOrderAdapterTest {

    @Mock
    private ILogOrderRepository logOrderRepository;

    @Mock
    private ILogOrderEntityMapper logOrderEntityMapper;

    @InjectMocks
    private LogOrderAdapter logOrderAdapter;

    @Test
    public void test_save_log_successfully() {
        // Arrange
        LogOrder logOrder = new LogOrder("123", "456", "789", "012", "PENDING", LocalDateTime.now());
        LogOrderEntity logOrderEntity = new LogOrderEntity();

        when(logOrderEntityMapper.toEntity(logOrder)).thenReturn(logOrderEntity);

        // Act
        logOrderAdapter.saveLog(logOrder);

        // Assert
        verify(logOrderEntityMapper).toEntity(logOrder);
        verify(logOrderRepository).save(logOrderEntity);
    }

    @Test
    public void test_get_history_order_returns_log_responses() {
        // Arrange
        String pedidoId = "123";
        LocalDateTime now = LocalDateTime.now();

        LogOrderEntity logEntity = new LogOrderEntity();
        logEntity.setEstado("PENDING");
        logEntity.setFechaCambio(now);

        List<LogOrderEntity> mockLogs = Arrays.asList(logEntity);

        when(logOrderRepository.findByPedidoId(pedidoId)).thenReturn(mockLogs);

        // Act
        List<LogOrderResponse> result = logOrderAdapter.getHistoryOrder(pedidoId);

        // Assert
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getEstado());
        assertEquals(now, result.get(0).getFechaCambio());

        verify(logOrderRepository).findByPedidoId(pedidoId);
    }

    @Test
    public void test_get_history_order_returns_logs_for_valid_pedido_id() {
        // Arrange
        String pedidoId = "123";
        LogOrderEntity logEntity = new LogOrderEntity();
        logEntity.setEstado("PENDING");
        logEntity.setFechaCambio(LocalDateTime.now());
        List<LogOrderEntity> mockLogs = List.of(logEntity);

        when(logOrderRepository.findByPedidoId(pedidoId)).thenReturn(mockLogs);

        // Act
        List<LogOrderResponse> result = logOrderAdapter.getHistoryOrder(pedidoId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(logEntity.getEstado(), result.get(0).getEstado());
        assertEquals(logEntity.getFechaCambio(), result.get(0).getFechaCambio());

        verify(logOrderRepository).findByPedidoId(pedidoId);
    }

    @Test
    public void test_get_order_logs_returns_list_when_valid_pedido_id() {
        // Arrange
        String pedidoId = "123";
        LogOrderEntity logOrderEntity = new LogOrderEntity();
        logOrderEntity.setPedidoId(pedidoId);
        logOrderEntity.setClienteId("456");
        logOrderEntity.setEstado("PENDING");

        LogOrder expectedLogOrder = new LogOrder(pedidoId, "456", null, null, "PENDING", null);

        List<LogOrderEntity> entityList = Collections.singletonList(logOrderEntity);

        when(logOrderRepository.findByPedidoId(pedidoId)).thenReturn(entityList);
        when(logOrderEntityMapper.toDomain(logOrderEntity)).thenReturn(expectedLogOrder);

        // Act
        List<LogOrder> result = logOrderAdapter.getOrderLogs(pedidoId);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(expectedLogOrder, result.get(0));
        verify(logOrderRepository).findByPedidoId(pedidoId);
        verify(logOrderEntityMapper).toDomain(logOrderEntity);
    }

    @Test
    public void test_get_filtered_logs_for_valid_restaurant() {
        // Arrange
        String restaurantId = "rest123";
        LogOrderEntity log1 = new LogOrderEntity();
        log1.setRestauranteId(restaurantId);
        log1.setEstado(OrderStatus.IN_PROGRESS);

        LogOrderEntity log2 = new LogOrderEntity();
        log2.setRestauranteId(restaurantId);
        log2.setEstado(OrderStatus.READY);

        List<LogOrderEntity> mockLogs = Arrays.asList(log1, log2);

        when(logOrderRepository.findAll()).thenReturn(mockLogs);
        when(logOrderEntityMapper.toDomain(any(LogOrderEntity.class)))
                .thenAnswer(i -> {
                    LogOrderEntity entity = i.getArgument(0);
                    return new LogOrder(null, null, entity.getRestauranteId(),
                            null, entity.getEstado(), null);
                });

        // Act
        List<LogOrder> result = logOrderAdapter.getAllOrderLogs(restaurantId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(log -> log.getRestauranteId().equals(restaurantId)));
        assertTrue(result.stream()
                .noneMatch(log -> log.getEstado().equalsIgnoreCase(OrderStatus.PENDING)));
    }

    @Test
    public void test_returns_empty_list_when_all_orders_are_pending() {
        // Arrange
        String restaurantId = "rest123";
        LogOrderEntity order1 = new LogOrderEntity();
        order1.setRestauranteId(restaurantId);
        order1.setEstado(OrderStatus.PENDING);

        LogOrderEntity order2 = new LogOrderEntity();
        order2.setRestauranteId(restaurantId);
        order2.setEstado(OrderStatus.PENDING);

        List<LogOrderEntity> mockOrders = Arrays.asList(order1, order2);

        when(logOrderRepository.findAll()).thenReturn(mockOrders);

        // Act
        List<LogOrder> result = logOrderAdapter.getAllOrderLogs(restaurantId);

        // Assert
        assertTrue(result.isEmpty());
        verify(logOrderRepository).findAll();
    }

    @Test
    public void test_delete_logs_with_valid_pedido_id() {
        // Arrange
        String pedidoId = "valid-pedido-123";

        // Act
        logOrderAdapter.deleteLogsByPedidoId(pedidoId);

        // Assert
        verify(logOrderRepository, times(1)).deleteByPedidoId(pedidoId);
    }

    @Test
    public void test_exists_by_pedido_id_returns_true_when_order_exists() {
        // Arrange
        String pedidoId = "123";
        ILogOrderRepository logOrderRepository = mock(ILogOrderRepository.class);
        LogOrderAdapter logOrderAdapter = new LogOrderAdapter(logOrderRepository, null);
        when(logOrderRepository.existsByPedidoId(pedidoId)).thenReturn(true);

        // Act
        boolean result = logOrderAdapter.existsByPedidoId(pedidoId);

        // Assert
        assertTrue(result);
        verify(logOrderRepository).existsByPedidoId(pedidoId);
    }

    @Test
    public void test_get_logs_returns_list_when_logs_exist() {
        // Arrange
        String pedidoId = "123";
        LogOrderEntity entity1 = new LogOrderEntity();
        entity1.setPedidoId(pedidoId);
        LogOrderEntity entity2 = new LogOrderEntity();
        entity2.setPedidoId(pedidoId);
        List<LogOrderEntity> entities = Arrays.asList(entity1, entity2);

        LogOrder logOrder1 = new LogOrder(pedidoId, "client1", "rest1", "chef1", "PENDING", LocalDateTime.now());
        LogOrder logOrder2 = new LogOrder(pedidoId, "client2", "rest2", "chef2", "COMPLETED", LocalDateTime.now());

        when(logOrderRepository.findByPedidoId(pedidoId)).thenReturn(entities);
        when(logOrderEntityMapper.toDomain(entity1)).thenReturn(logOrder1);
        when(logOrderEntityMapper.toDomain(entity2)).thenReturn(logOrder2);

        // Act
        List<LogOrder> result = logOrderAdapter.getLogsByPedidoId(pedidoId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
