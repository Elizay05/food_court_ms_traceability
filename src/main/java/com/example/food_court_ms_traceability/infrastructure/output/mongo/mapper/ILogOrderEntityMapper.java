package com.example.food_court_ms_traceability.infrastructure.output.mongo.mapper;

import com.example.food_court_ms_traceability.domain.model.LogOrder;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.entity.LogOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ILogOrderEntityMapper {

    LogOrder toDomain(LogOrderEntity logOrderEntity);

    LogOrderEntity toEntity(LogOrder logOrder);
}
