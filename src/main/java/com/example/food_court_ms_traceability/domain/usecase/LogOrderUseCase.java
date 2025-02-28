package com.example.food_court_ms_traceability.domain.usecase;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.api.ILogOrderServicePort;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.domain.spi.ILogOrderPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class LogOrderUseCase implements ILogOrderServicePort {

    private final ILogOrderPersistencePort logOrderPersistencePort;

    @Override
    public void registerChangeState(LogOrder logOrder) {
        logOrder.setFechaCambio(LocalDateTime.now());
        logOrderPersistencePort.saveLog(logOrder);
    }

    @Override
    public List<LogOrderResponse> getHistoryOrder(String pedidoId, String clienteId) {
        return logOrderPersistencePort.getHistoryOrder(pedidoId, clienteId);
    }
}
