package com.example.food_court_ms_traceability.infrastructure.exceptionHandler;

import com.example.food_court_ms_traceability.infrastructure.exception.OrderHistoryNotFoundException;
import com.example.food_court_ms_traceability.infrastructure.exception.UnauthorizedAccessException;
import com.example.food_court_ms_traceability.infrastructure.util.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleOrderHistoryNotFoundException() {
        OrderHistoryNotFoundException exception = new OrderHistoryNotFoundException(ErrorMessages.HISTORY_ORDER_NOT_FOUND);

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleOrderHistoryNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorMessages.HISTORY_ORDER_NOT_FOUND, response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleUnauthorizedAccessException() {
        UnauthorizedAccessException exception = new UnauthorizedAccessException(ErrorMessages.UNAUTHORIZED_ACCESS);

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleUnauthorizedAccessException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ErrorMessages.UNAUTHORIZED_ACCESS, response.getBody().getMessage());
        assertEquals(HttpStatus.FORBIDDEN.toString(), response.getBody().getStatus());
    }
}
