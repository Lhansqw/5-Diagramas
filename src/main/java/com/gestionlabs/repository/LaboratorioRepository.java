package com.gestionlabs.repository;

import com.gestionlabs.model.Laboratorio;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LaboratorioRepository extends MongoRepository<Laboratorio, String> {
}
