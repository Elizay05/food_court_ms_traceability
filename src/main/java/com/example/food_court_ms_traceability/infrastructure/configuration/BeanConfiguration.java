package com.example.food_court_ms_traceability.infrastructure.configuration;

import com.example.food_court_ms_traceability.domain.api.ILogOrderServicePort;
import com.example.food_court_ms_traceability.domain.spi.ILogOrderPersistencePort;
import com.example.food_court_ms_traceability.domain.usecase.LogOrderUseCase;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.adapter.LogOrderAdapter;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.mapper.ILogOrderEntityMapper;
import com.example.food_court_ms_traceability.infrastructure.output.mongo.repository.ILogOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final ILogOrderRepository logOrderRepository;
    private final ILogOrderEntityMapper logOrderEntityMapper;

    @Bean
    public ILogOrderServicePort logOrderServicePort() {
        return new LogOrderUseCase(logOrderPersistencePort());
    }

    @Bean
    public ILogOrderPersistencePort logOrderPersistencePort() {
        return new LogOrderAdapter(logOrderRepository, logOrderEntityMapper);
    }
}
