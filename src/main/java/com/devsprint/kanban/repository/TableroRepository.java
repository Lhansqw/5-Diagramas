package com.devsprint.kanban.repository;

import com.devsprint.kanban.model.Tablero;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableroRepository extends MongoRepository<Tablero, String> {
    List<Tablero> findByPropietarioId(String propietarioId);
    List<Tablero> findByNombreContainingIgnoreCase(String nombre);
}
