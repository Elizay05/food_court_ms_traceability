package com.example.food_court_ms_traceability.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEfficiencyResponse {
    private String pedidoId;
    private long minutes;
    private long seconds;
}
