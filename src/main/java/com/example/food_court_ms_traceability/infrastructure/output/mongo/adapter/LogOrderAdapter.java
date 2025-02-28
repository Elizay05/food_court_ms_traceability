package com.example.food_court_ms_traceability.infrastructure.output.mongo.adapter;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.domain.spi.ILogOrderPersistencePort;
import com.example.food_court_ms_traceability.infrastructure.exception.OrderHistoryNotFoundException;
import com.example.food_court_ms_traceability.infrastructure.exception.UnauthorizedAccessException;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.entity.LogOrderEntity;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.mapper.ILogOrderEntityMapper;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.repository.ILogOrderRepository;
import com.example.food_court_ms_traceability.infrastructure.util.ErrorMessages;
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
    public List<LogOrderResponse> getHistoryOrder(String pedidoId, String clienteId) {
        List<LogOrderEntity> logs = logOrderRepository.findByPedidoId(pedidoId);

        if (logs.isEmpty()) {
            throw new OrderHistoryNotFoundException(ErrorMessages.HISTORY_ORDER_NOT_FOUND);
        }

        boolean allMatch = logs.stream()
                .allMatch(log -> log.getClienteId().equals(clienteId));

        if (!allMatch) {
            throw new UnauthorizedAccessException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        return logs.stream()
                .map(log -> new LogOrderResponse(log.getEstado(), log.getFechaCambio()))
                .collect(Collectors.toList());
    }
}
