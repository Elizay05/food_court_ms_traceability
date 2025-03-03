package com.example.food_court_ms_traceability.domain.model;

public class OrderEfficiency {
    private String pedidoId;
    private long minutes;
    private long seconds;

    public OrderEfficiency(String pedidoId, long minutes, long seconds) {
        this.pedidoId = pedidoId;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }
}
