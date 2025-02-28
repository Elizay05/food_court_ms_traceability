package com.example.food_court_ms_traceability.application.handler.impl;

import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.application.handler.ILogOrderHandler;
import com.example.food_court_ms_traceability.application.mapper.ILogOrderRequestMapper;
import com.example.food_court_ms_traceability.domain.api.ILogOrderServicePort;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.infrastructure.configuration.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LogOrderHandler implements ILogOrderHandler {

    private final ILogOrderServicePort logOrderServicePort;
    private final ILogOrderRequestMapper logOrderRequestMapper;

    @Override
    public void registerChangeState(LogOrderRequest request) {
        LogOrder logOrder = logOrderRequestMapper.toDomain(request);
        logOrderServicePort.registerChangeState(logOrder);
    }

    @Override
    public List<LogOrderResponse> getHistoryOrder(String pedidoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return logOrderServicePort.getHistoryOrder(pedidoId, userDetails.getDocumentNumber());
    }
}
