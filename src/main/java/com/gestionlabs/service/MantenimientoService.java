package com.gestionlabs.service;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.model.Laboratorio;
import com.gestionlabs.model.Mantenimiento;
import com.gestionlabs.repository.EquipoRepository;
import com.gestionlabs.repository.LaboratorioRepository;
import com.gestionlabs.repository.MantenimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class MantenimientoService {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    public Mantenimiento programarMantenimiento(String equipoId, Mantenimiento mantenimiento) {
        if (mantenimiento.getId() == null || mantenimiento.getId().isEmpty()) {
            mantenimiento.setId(UUID.randomUUID().toString());
        }

        Mantenimiento savedMantenimiento = mantenimientoRepository.save(mantenimiento);

        Optional<Equipo> eqOpt = equipoRepository.findById(equipoId);
        if (eqOpt.isPresent()) {
            Equipo eq = eqOpt.get();
            eq.setEstado("MANTENIMIENTO");
            eq.getMantenimientos().add(savedMantenimiento);
            equipoRepository.save(eq);

            for (Laboratorio lab : laboratorioRepository.findAll()) {
                boolean found = false;
                for (Equipo e : lab.getEquipos()) {
                    if (e.getId().equals(equipoId)) {
                        e.setEstado("MANTENIMIENTO");
                        e.getMantenimientos().add(savedMantenimiento);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    laboratorioRepository.save(lab);
                    break;
                }
            }
            return savedMantenimiento;
        }
        throw new RuntimeException("Equipo no encontrado con ID: " + equipoId);
    }
}
