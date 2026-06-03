package com.gestionlabs.repository;

import com.gestionlabs.model.Mantenimiento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MantenimientoRepository extends MongoRepository<Mantenimiento, String> {
}
