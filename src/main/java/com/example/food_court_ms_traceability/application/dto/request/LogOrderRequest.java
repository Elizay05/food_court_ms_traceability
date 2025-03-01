package com.example.food_court_ms_traceability.application.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogOrderRequest {
    @NotBlank(message = "El ID del pedido es obligatorio")
    private String pedidoId;

    @NotBlank(message = "El ID del cliente es obligatorio")
    private String clienteId;

    @NotBlank(message = "El ID del restaurante es obligatorio")
    private String restauranteId;

    private String chefId;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;
}
