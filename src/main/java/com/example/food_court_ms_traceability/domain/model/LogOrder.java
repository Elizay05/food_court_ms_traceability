package com.example.food_court_ms_traceability.domain.model;

import java.time.LocalDateTime;

public class LogOrder {
    private String pedidoId;
    private String clienteId;
    private String estado;
    private LocalDateTime fechaCambio;

    public LogOrder(String pedidoId, String clienteId, String estado, LocalDateTime fechaCambio) {
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.estado = estado;
        this.fechaCambio = fechaCambio;
    }

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}
