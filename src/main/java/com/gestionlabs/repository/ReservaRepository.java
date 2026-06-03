package com.gestionlabs.repository;

import com.gestionlabs.model.Reserva;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReservaRepository extends MongoRepository<Reserva, String> {
    List<Reserva> findByUsuarioId(String usuarioId);
    List<Reserva> findByLaboratorioId(String laboratorioId);
}
