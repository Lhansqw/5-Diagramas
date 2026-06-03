package com.gestionlabs.controller;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.model.FranjaHoraria;
import com.gestionlabs.model.Laboratorio;
import com.gestionlabs.model.Reserva;
import com.gestionlabs.repository.ReservaRepository;
import com.gestionlabs.service.LaboratorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioController {

    @Autowired
    private LaboratorioService laboratorioService;

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping
    public ResponseEntity<List<Laboratorio>> listar() {
        return ResponseEntity.ok(laboratorioService.listarLaboratorios());
    }

    @PostMapping
    public ResponseEntity<Laboratorio> crear(@RequestBody Laboratorio lab) {
        return ResponseEntity.ok(laboratorioService.crearLaboratorio(lab));
    }

    @PostMapping("/{id}/equipos")
    public ResponseEntity<Laboratorio> agregarEquipo(@PathVariable String id, @RequestBody Equipo equipo) {
        return ResponseEntity.ok(laboratorioService.agregarEquipo(id, equipo));
    }

    /**
     * GET /api/laboratorios/{id}/franjas?fecha=YYYY-MM-DD
     * Devuelve las franjas horarias disponibles (no reservadas y no canceladas)
     * para el laboratorio en la fecha indicada.
     * Si no se indica fecha, se usa la de hoy.
     */
    @GetMapping("/{id}/franjas")
    public ResponseEntity<List<FranjaHoraria>> franjasDisponibles(
            @PathVariable String id,
            @RequestParam(required = false) String fecha) {

        String fechaConsulta = (fecha != null && !fecha.isEmpty())
            ? fecha
            : java.time.LocalDate.now().toString();

        // Franjas estándar del día (bloques de 2 horas, 7 AM – 9 PM)
        List<FranjaHoraria> todasLasFranjas = new ArrayList<>();
        String[] horas = {"07:00","09:00","11:00","13:00","15:00","17:00","19:00","21:00"};
        for (int i = 0; i < horas.length - 1; i++) {
            todasLasFranjas.add(new FranjaHoraria(fechaConsulta, horas[i], horas[i + 1]));
        }

        // Franjas ya ocupadas (reservas activas para ese laboratorio y fecha)
        List<Reserva> reservasLab = reservaRepository.findByLaboratorioId(id);
        Set<String> ocupadas = reservasLab.stream()
            .filter(r -> !"CANCELADA".equals(r.getEstado()))
            .filter(r -> r.getFranjaHoraria() != null)
            .filter(r -> fechaConsulta.equals(r.getFranjaHoraria().getFecha()))
            .map(r -> r.getFranjaHoraria().getHoraInicio() + "-" + r.getFranjaHoraria().getHoraFin())
            .collect(Collectors.toSet());

        List<FranjaHoraria> disponibles = todasLasFranjas.stream()
            .filter(f -> !ocupadas.contains(f.getHoraInicio() + "-" + f.getHoraFin()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(disponibles);
    }
}
