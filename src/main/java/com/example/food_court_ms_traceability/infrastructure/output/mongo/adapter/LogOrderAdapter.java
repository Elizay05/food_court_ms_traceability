package com.example.food_court_ms_traceability.infrastructure.output.mongo.adapter;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.domain.spi.ILogOrderPersistencePort;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.entity.LogOrderEntity;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.mapper.ILogOrderEntityMapper;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.repository.ILogOrderRepository;
import com.example.food_court_ms_traceability.infrastructure.util.OrderStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LogOrderAdapter implements ILogOrderPersistencePort {

    private final ILogOrderRepository logOrderRepository;
    private final ILogOrderEntityMapper logOrderEntityMapper;

    @Override
    public void saveLog(LogOrder logOrder) {
        LogOrderEntity logOrderEntity = logOrderEntityMapper.toEntity(logOrder);
        logOrderRepository.save(logOrderEntity);
    }

    @Override
    public List<LogOrderResponse> getHistoryOrder(String pedidoId) {
        List<LogOrderEntity> logs = logOrderRepository.findByPedidoId(pedidoId);
        return logs.stream()
                .map(log -> new LogOrderResponse(log.getEstado(), log.getFechaCambio()))
                .collect(Collectors.toList());
    }

    @Override
    public List<LogOrder> getOrderLogs(String pedidoId) {
        List<LogOrderEntity> logOrder = logOrderRepository.findByPedidoId(pedidoId);
        return logOrder.stream()
                .map(logOrderEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<LogOrder> getAllOrderLogs(String restauranteId) {
        List<LogOrderEntity> logs = logOrderRepository.findAll();

        return logs.stream()
                .filter(log -> !log.getEstado().equalsIgnoreCase(OrderStatus.PENDING))
                .filter(log -> log.getRestauranteId().equals(restauranteId))
                .map(logOrderEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLogsByPedidoId(String pedidoId) {
        logOrderRepository.deleteByPedidoId(pedidoId);
    }

    @Override
    public boolean existsByPedidoId(String pedidoId) {
        return logOrderRepository.existsByPedidoId(pedidoId);
    }

    @Override
    public List<LogOrder> getLogsByPedidoId(String pedidoId) {
        List<LogOrderEntity> logEntities = logOrderRepository.findByPedidoId(pedidoId);

        return logEntities.stream()
                .map(logOrderEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
