package com.example.food_court_ms_traceability.application.handler;

import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.application.dto.response.EmployeeEfficiencyResponse;
import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.application.dto.response.OrderEfficiencyResponse;

import java.util.List;

public interface ILogOrderHandler {
    void registerChangeState(LogOrderRequest request);
    List<LogOrderResponse> getHistoryOrder(String pedidoId);
    OrderEfficiencyResponse getOrderEfficiency(String pedidoId);
    List<EmployeeEfficiencyResponse> getEmployeeEfficiencyRanking();
}
