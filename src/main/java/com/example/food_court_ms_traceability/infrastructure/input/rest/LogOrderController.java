package com.example.food_court_ms_traceability.infrastructure.input.rest;

import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.application.handler.ILogOrderHandler;
import com.example.food_court_ms_traceability.infrastructure.util.RoleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/traceability")
@RequiredArgsConstructor
public class LogOrderController {

    private final ILogOrderHandler logOrderHandler;

    @Operation(
            summary = "Register an order status change",
            description = "Allows customers and employees to register a status change for an order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status change successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    @PreAuthorize("hasRole('" + RoleConstants.CUSTOMER + "') or hasRole('" + RoleConstants.EMPLOYEE + "')")
    public void registerChangeState(@Valid @RequestBody LogOrderRequest request) {
        logOrderHandler.registerChangeState(request);
    }

    @Operation(
            summary = "Get order history",
            description = "Allows an authenticated customer to retrieve the status change history of an order."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order history successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Forbidden access"),
            @ApiResponse(responseCode = "404", description = "Order history not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/order/{pedidoId}")
    @PreAuthorize("hasRole('" + RoleConstants.CUSTOMER + "')")
    public List<LogOrderResponse> getHistoryOrder(@PathVariable String pedidoId) {
        return logOrderHandler.getHistoryOrder(pedidoId);
    }
}
