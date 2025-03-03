package com.example.food_court_ms_traceability.domain.usecase;

import com.example.food_court_ms_traceability.application.dto.response.LogOrderResponse;
import com.example.food_court_ms_traceability.domain.api.ILogOrderServicePort;
import com.example.food_court_ms_traceability.domain.exception.OrderLogsNotFoundException;
import com.example.food_court_ms_traceability.domain.exception.UnauthorizedAccessException;
import com.example.food_court_ms_traceability.domain.model.EmployeeEfficiency;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.domain.model.OrderEfficiency;
import com.example.food_court_ms_traceability.domain.spi.ILogOrderPersistencePort;
import com.example.food_court_ms_traceability.domain.util.ExceptionConstants;
import com.example.food_court_ms_traceability.domain.util.OrderStatus;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LogOrderUseCase implements ILogOrderServicePort {

    private final ILogOrderPersistencePort logOrderPersistencePort;

    @Override
    public void registerChangeState(LogOrder logOrder) {
        logOrder.setFechaCambio(LocalDateTime.now());
        logOrderPersistencePort.saveLog(logOrder);
    }

    @Override
    public List<LogOrderResponse> getHistoryOrder(String pedidoId, String clienteId) {
        boolean exists = logOrderPersistencePort.existsByPedidoId(pedidoId);
        if (!exists) {
            throw new OrderLogsNotFoundException(ExceptionConstants.HISTORY_ORDER_NOT_FOUND);
        }

        List<LogOrder> logs = logOrderPersistencePort.getLogsByPedidoId(pedidoId);
        boolean isAssociated = logs.stream().anyMatch(log -> log.getClienteId().equals(clienteId));

        if (!isAssociated) {
            throw new UnauthorizedAccessException(ExceptionConstants.UNAUTHORIZED_HISTORY_ACCESS);
        }
        return logOrderPersistencePort.getHistoryOrder(pedidoId);
    }

    @Override
    public OrderEfficiency getOrderEfficiency(String pedidoId, String restauranteId) {
        boolean exists = logOrderPersistencePort.existsByPedidoId(pedidoId);
        if (!exists) {
            throw new OrderLogsNotFoundException(ExceptionConstants.HISTORY_ORDER_NOT_FOUND);
        }
        List<LogOrder> logs = logOrderPersistencePort.getOrderLogs(pedidoId);

        boolean allMatch = logs.stream()
                .allMatch(log -> log.getRestauranteId().equals(restauranteId));

        if (!allMatch) {
            throw new UnauthorizedAccessException(ExceptionConstants.UNAUTHORIZED_ORDER_EFFICIENCY_ACCESS);
        }

        LocalDateTime startTime = logs.get(0).getFechaCambio();
        LocalDateTime endTime = logs.get(logs.size() - 1).getFechaCambio();

        Duration duration = Duration.between(startTime, endTime);

        long minutes = duration.toMinutes();
        long seconds = duration.toSecondsPart();

        return new OrderEfficiency(pedidoId, minutes, seconds);
    }

    @Override
    public List<EmployeeEfficiency> getEmployeeEfficiencyRanking(String restauranteId) {
        List<LogOrder> allLogs = logOrderPersistencePort.getAllOrderLogs(restauranteId);

        Map<String, List<LogOrder>> employeeLogs = allLogs.stream()
                .filter(log -> log.getChefId() != null && !log.getChefId().isEmpty())
                .collect(Collectors.groupingBy(LogOrder::getChefId));

        return employeeLogs.entrySet().stream()
                .map(entry -> {
                    String chefId = entry.getKey();
                    List<LogOrder> logs = entry.getValue();

                    Map<String, List<LogOrder>> pedidosPorChef = logs.stream()
                            .collect(Collectors.groupingBy(LogOrder::getPedidoId));

                    List<Duration> durations = pedidosPorChef.values().stream()
                            .map(pedidoLogs -> {
                                pedidoLogs.sort(Comparator.comparing(LogOrder::getFechaCambio));

                                LocalDateTime inicio = null, fin = null;
                                for (LogOrder log : pedidoLogs) {
                                    if (OrderStatus.IN_PROGRESS.equals(log.getEstado())) {
                                        inicio = log.getFechaCambio();
                                    } else if (OrderStatus.READY.equals(log.getEstado())) {
                                        fin = log.getFechaCambio();
                                        break;
                                    }
                                }

                                return (inicio != null && fin != null) ? Duration.between(inicio, fin) : null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    double avgSeconds = durations.stream()
                            .mapToLong(Duration::toSeconds)
                            .average()
                            .orElse(0);

                    long minutes = (long) avgSeconds / 60;
                    long seconds = (long) avgSeconds % 60;

                    return new EmployeeEfficiency(chefId, minutes, seconds);
                })
                .sorted(Comparator.comparingLong(EmployeeEfficiency::getMinutes)
                        .thenComparingLong(EmployeeEfficiency::getSeconds))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrderLogs(String pedidoId, String clienteId) {
        boolean exists = logOrderPersistencePort.existsByPedidoId(pedidoId);
        if (!exists) {
            throw new OrderLogsNotFoundException(ExceptionConstants.HISTORY_ORDER_NOT_FOUND);
        }

        List<LogOrder> logs = logOrderPersistencePort.getLogsByPedidoId(pedidoId);
        boolean isAssociated = logs.stream().anyMatch(log -> log.getClienteId().equals(clienteId));

        if (!isAssociated) {
            throw new UnauthorizedAccessException(ExceptionConstants.UNAUTHORIZED_ORDER_DELETE_ACCESS);
        }

        logOrderPersistencePort.deleteLogsByPedidoId(pedidoId);
    }
}
