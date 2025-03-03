package com.example.food_court_ms_traceability.infrastructure.output.mongo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "logs_order")
@Data
public class LogOrderEntity {
    @Id
    private String id;
    private String clienteId;
    private String pedidoId;
    private String restauranteId;
    private String chefId;
    private String estado;
    private LocalDateTime fechaCambio;
}
