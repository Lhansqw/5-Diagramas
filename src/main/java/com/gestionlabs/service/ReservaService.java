package com.gestionlabs.service;

import com.gestionlabs.dto.ReservaDTO;
import com.gestionlabs.model.Reserva;
import com.gestionlabs.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public Reserva crearReserva(ReservaDTO dto) {
        Reserva reserva = new Reserva();
        reserva.setUsuarioId(dto.getUsuarioId());
        reserva.setLaboratorioId(dto.getLaboratorioId());
        reserva.setEquipoId(dto.getEquipoId());
        reserva.setFranjaHoraria(dto.getFranjaHoraria());
        reserva.setEstado("PENDIENTE");
        reserva.setFechaReserva(new Date());
        return reservaRepository.save(reserva);
    }

    public List<Reserva> obtenerReservasUsuario(String usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
    
    public List<Reserva> obtenerReservasLaboratorio(String labId) {
        return reservaRepository.findByLaboratorioId(labId);
    }

    public void cancelarReserva(String id) {
        Optional<Reserva> r = reservaRepository.findById(id);
        if (r.isPresent()) {
            Reserva res = r.get();
            res.setEstado("CANCELADA");
            reservaRepository.save(res);
        }
    }
}
