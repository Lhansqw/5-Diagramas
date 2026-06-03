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

    @Autowired
    private NotificacionService notificacionService;

    public Reserva crearReserva(ReservaDTO dto) {
        // Validación de disponibilidad: comprobar que la franja horaria en la fecha no esté ya ocupada
        List<Reserva> existentes = reservaRepository.findByLaboratorioId(dto.getLaboratorioId());
        boolean ocupado = existentes.stream().anyMatch(r -> 
            !"CANCELADA".equals(r.getEstado()) &&
            r.getFranjaHoraria() != null &&
            dto.getFranjaHoraria() != null &&
            r.getFranjaHoraria().getFecha().equals(dto.getFranjaHoraria().getFecha()) &&
            r.getFranjaHoraria().getHoraInicio().equals(dto.getFranjaHoraria().getHoraInicio()) &&
            r.getFranjaHoraria().getHoraFin().equals(dto.getFranjaHoraria().getHoraFin())
        );

        if (ocupado) {
            throw new RuntimeException("La franja horaria seleccionada ya está reservada para este laboratorio.");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuarioId(dto.getUsuarioId());
        reserva.setLaboratorioId(dto.getLaboratorioId());
        reserva.setEquipoId(dto.getEquipoId());
        reserva.setFranjaHoraria(dto.getFranjaHoraria());
        reserva.setEstado("PENDIENTE");
        reserva.setFechaReserva(new Date());
        
        Reserva saved = reservaRepository.save(reserva);
        
        // Enviar notificación
        notificacionService.enviarNotificacion(dto.getUsuarioId(), 
            "Tu reserva para el laboratorio " + dto.getLaboratorioId() + " en la fecha " + 
            dto.getFranjaHoraria().getFecha() + " (" + dto.getFranjaHoraria().getHoraInicio() + 
            "-" + dto.getFranjaHoraria().getHoraFin() + ") ha sido registrada con estado PENDIENTE.");

        return saved;
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
            
            // Enviar notificación de cancelación
            notificacionService.enviarNotificacion(res.getUsuarioId(), 
                "Tu reserva para el laboratorio " + res.getLaboratorioId() + " el " + 
                (res.getFranjaHoraria() != null ? res.getFranjaHoraria().getFecha() : "") + " ha sido CANCELADA.");
        }
    }

    public void eliminarReserva(String id) {
        reservaRepository.deleteById(id);
    }
}
