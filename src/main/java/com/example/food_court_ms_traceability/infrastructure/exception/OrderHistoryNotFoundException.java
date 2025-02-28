package com.example.food_court_ms_traceability.infrastructure.exception;

public class OrderHistoryNotFoundException extends RuntimeException {
    public OrderHistoryNotFoundException(String message) {
        super(message);
    }
}
