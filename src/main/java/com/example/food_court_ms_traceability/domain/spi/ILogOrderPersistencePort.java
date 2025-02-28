package com.example.food_court_ms_traceability.domain.spi;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.model.LogOrder;

import java.util.List;

public interface ILogOrderPersistencePort {
    void saveLog(LogOrder logOrder);
    List<LogOrderResponse> getHistoryOrder(String pedidoId, String clienteId);
}
