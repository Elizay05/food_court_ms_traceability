package com.example.food_court_ms_traceability.domain.api;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.model.LogOrder;

import java.util.List;

public interface ILogOrderServicePort {
    void registerChangeState(LogOrder logOrder);
    List<LogOrderResponse> getHistoryOrder(String pedidoId, String clienteId);
}
