package com.example.food_court_ms_traceability.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogOrderResponse {
    private String estado;
    private LocalDateTime fechaCambio;
}
