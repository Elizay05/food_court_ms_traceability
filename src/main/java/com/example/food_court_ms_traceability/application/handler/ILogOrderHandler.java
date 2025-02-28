package com.example.food_court_ms_traceability.application.handler;

import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;

import java.util.List;

public interface ILogOrderHandler {
    void registerChangeState(LogOrderRequest request);
    List<LogOrderResponse> getHistoryOrder(String pedidoId);
}
