package com.example.food_court_ms_traceability.application.mapper;


import com.example.food_court_ms_traceability.application.dto.request.LogOrderRequest;
import com.example.food_court_ms_traceability.domain.model.LogOrder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ILogOrderRequestMapper {
    LogOrder toDomain(LogOrderRequest logOrderRequest);
    LogOrderRequest toRequestDto(LogOrder logOrder);
}
