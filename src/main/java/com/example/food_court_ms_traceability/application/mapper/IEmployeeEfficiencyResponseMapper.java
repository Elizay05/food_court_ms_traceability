package com.example.food_court_ms_traceability.application.mapper;

import com.example.food_court_ms_traceability.application.dto.response.EmployeeEfficiencyResponse;
import com.example.food_court_ms_traceability.domain.model.EmployeeEfficiency;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IEmployeeEfficiencyResponseMapper {
    List<EmployeeEfficiencyResponse> toResponse(List<EmployeeEfficiency> employeeEfficiency);
}
