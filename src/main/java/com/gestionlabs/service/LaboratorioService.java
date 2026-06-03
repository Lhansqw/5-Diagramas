package com.gestionlabs.service;

import com.gestionlabs.model.Laboratorio;
import com.gestionlabs.model.Equipo;
import com.gestionlabs.repository.LaboratorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LaboratorioService {

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    public List<Laboratorio> listarLaboratorios() {
        return laboratorioRepository.findAll();
    }

    public Laboratorio crearLaboratorio(Laboratorio lab) {
        return laboratorioRepository.save(lab);
    }

    public Laboratorio agregarEquipo(String labId, Equipo equipo) {
        Optional<Laboratorio> labOpt = laboratorioRepository.findById(labId);
        if (labOpt.isPresent()) {
            Laboratorio lab = labOpt.get();
            lab.getEquipos().add(equipo);
            return laboratorioRepository.save(lab);
        }
        throw new RuntimeException("Laboratorio no encontrado");
    }
}
