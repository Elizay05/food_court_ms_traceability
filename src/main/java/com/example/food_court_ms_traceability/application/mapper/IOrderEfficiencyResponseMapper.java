package com.example.food_court_ms_traceability.application.mapper;

import com.example.food_court_ms_traceability.application.dto.response.OrderEfficiencyResponse;
import com.example.food_court_ms_traceability.domain.model.OrderEfficiency;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderEfficiencyResponseMapper {
    OrderEfficiencyResponse toResponse(OrderEfficiency orderEfficiency);
}
