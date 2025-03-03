package com.example.food_court_ms_traceability.domain.exception;

public class OrderLogsNotFoundException extends RuntimeException {
    public OrderLogsNotFoundException(String message) {
        super(message);
    }
}
