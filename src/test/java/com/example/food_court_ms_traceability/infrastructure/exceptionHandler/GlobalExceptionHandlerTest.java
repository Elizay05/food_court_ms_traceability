package com.example.food_court_ms_traceability.infrastructure.exceptionHandler;

import com.example.food_court_ms_traceability.domain.exception.OrderLogsNotFoundException;
import com.example.food_court_ms_traceability.domain.exception.UnauthorizedAccessException;
import com.example.food_court_ms_traceability.domain.util.ExceptionConstants;
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
    void shouldHandleOrderLogsNotFoundException() {
        OrderLogsNotFoundException exception = new OrderLogsNotFoundException(ExceptionConstants.HISTORY_ORDER_NOT_FOUND);

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleOrderLogsNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ExceptionConstants.HISTORY_ORDER_NOT_FOUND, response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleUnauthorizedAccessException() {
        UnauthorizedAccessException exception = new UnauthorizedAccessException(ExceptionConstants.UNAUTHORIZED_ORDER_EFFICIENCY_ACCESS);

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleUnauthorizedAccessException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ExceptionConstants.UNAUTHORIZED_ORDER_EFFICIENCY_ACCESS, response.getBody().getMessage());
        assertEquals(HttpStatus.FORBIDDEN.toString(), response.getBody().getStatus());
    }
}
