package com.example.food_court_ms_traceability.infrastructure.output.mongo.repository;

import com.example.food_court_ms_traceability.infrastructure.output.mongo.entity.LogOrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILogOrderRepository extends MongoRepository<LogOrderEntity, String> {
    List<LogOrderEntity> findByPedidoId(String pedidoId);
    List<LogOrderEntity> findAll();
    void deleteByPedidoId(String pedidoId);
    boolean existsByPedidoId(String pedidoId);
}
