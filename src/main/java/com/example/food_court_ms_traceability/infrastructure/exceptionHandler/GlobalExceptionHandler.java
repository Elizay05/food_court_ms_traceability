package com.example.food_court_ms_traceability.infrastructure.exceptionHandler;

import com.example.food_court_ms_traceability.domain.exception.OrderLogsNotFoundException;
import com.example.food_court_ms_traceability.domain.exception.UnauthorizedAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), HttpStatus.FORBIDDEN.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(OrderLogsNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleOrderLogsNotFoundException(OrderLogsNotFoundException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }
}
