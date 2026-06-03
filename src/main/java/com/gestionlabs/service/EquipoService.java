package com.gestionlabs.service;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.model.Laboratorio;
import com.gestionlabs.repository.EquipoRepository;
import com.gestionlabs.repository.LaboratorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    public List<Equipo> listarEquipos() {
        return equipoRepository.findAll();
    }

    public Optional<Equipo> obtenerEquipoPorId(String id) {
        return equipoRepository.findById(id);
    }

    public Equipo registrarEquipo(Equipo equipo, String laboratorioId) {
        if (equipo.getId() == null || equipo.getId().isEmpty()) {
            equipo.setId(java.util.UUID.randomUUID().toString());
        }
        if (equipo.getEstado() == null) {
            equipo.setEstado("DISPONIBLE");
        }
        
        Equipo saved = equipoRepository.save(equipo);

        if (laboratorioId != null && !laboratorioId.isEmpty()) {
            Optional<Laboratorio> labOpt = laboratorioRepository.findById(laboratorioId);
            if (labOpt.isPresent()) {
                Laboratorio lab = labOpt.get();
                lab.getEquipos().removeIf(e -> e.getId().equals(saved.getId()));
                lab.getEquipos().add(saved);
                laboratorioRepository.save(lab);
            } else {
                throw new RuntimeException("Laboratorio no encontrado con ID: " + laboratorioId);
            }
        }
        return saved;
    }

    public Equipo actualizarEstadoEquipo(String id, String estado) {
        Optional<Equipo> eqOpt = equipoRepository.findById(id);
        if (eqOpt.isPresent()) {
            Equipo eq = eqOpt.get();
            eq.setEstado(estado);
            Equipo saved = equipoRepository.save(eq);

            List<Laboratorio> laboratorios = laboratorioRepository.findAll();
            for (Laboratorio lab : laboratorios) {
                boolean found = false;
                for (Equipo e : lab.getEquipos()) {
                    if (e.getId().equals(id)) {
                        e.setEstado(estado);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    laboratorioRepository.save(lab);
                    break;
                }
            }
            return saved;
        }
        throw new RuntimeException("Equipo no encontrado con ID: " + id);
    }
}
