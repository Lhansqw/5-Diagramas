package com.gestionlabs.controller;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.model.FranjaHoraria;
import com.gestionlabs.model.Laboratorio;
import com.gestionlabs.model.Reserva;
import com.gestionlabs.repository.ReservaRepository;
import com.gestionlabs.service.LaboratorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/laboratorios")
@Tag(name = "Laboratorios", description = "Gestión de laboratorios y sus franjas horarias disponibles")
public class LaboratorioController {

    @Autowired
    private LaboratorioService laboratorioService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Operation(summary = "Listar todos los laboratorios")
    @ApiResponse(responseCode = "200", description = "Lista de laboratorios",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Laboratorio.class))))
    @GetMapping
    public ResponseEntity<List<Laboratorio>> listar() {
        return ResponseEntity.ok(laboratorioService.listarLaboratorios());
    }

    @Operation(summary = "Crear laboratorio", description = "Registra un nuevo laboratorio en el sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Laboratorio creado",
            content = @Content(schema = @Schema(implementation = Laboratorio.class)))
    })
    @PostMapping
    public ResponseEntity<Laboratorio> crear(@RequestBody Laboratorio lab) {
        return ResponseEntity.ok(laboratorioService.crearLaboratorio(lab));
    }

    @Operation(summary = "Agregar equipo a laboratorio", description = "Asocia un equipo nuevo o existente a un laboratorio específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo agregado al laboratorio",
            content = @Content(schema = @Schema(implementation = Laboratorio.class)))
    })
    @PostMapping("/{id}/equipos")
    public ResponseEntity<Laboratorio> agregarEquipo(
            @Parameter(description = "ID del laboratorio", required = true) @PathVariable String id,
            @RequestBody Equipo equipo) {
        return ResponseEntity.ok(laboratorioService.agregarEquipo(id, equipo));
    }

    @Operation(
        summary = "Franjas horarias disponibles",
        description = "Retorna los bloques de 2 horas disponibles (07:00–21:00) para el laboratorio en la fecha indicada. " +
            "Si no se especifica `fecha`, se usa la fecha actual. Las franjas con reservas activas quedan excluidas."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de franjas disponibles",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FranjaHoraria.class))))
    })
    @GetMapping("/{id}/franjas")
    public ResponseEntity<List<FranjaHoraria>> franjasDisponibles(
            @Parameter(description = "ID del laboratorio", required = true) @PathVariable String id,
            @Parameter(description = "Fecha en formato YYYY-MM-DD (por defecto: hoy)") @RequestParam(required = false) String fecha) {

        String fechaConsulta = (fecha != null && !fecha.isEmpty())
            ? fecha
            : java.time.LocalDate.now().toString();

        List<FranjaHoraria> todasLasFranjas = new ArrayList<>();
        String[] horas = {"07:00","09:00","11:00","13:00","15:00","17:00","19:00","21:00"};
        for (int i = 0; i < horas.length - 1; i++) {
            todasLasFranjas.add(new FranjaHoraria(fechaConsulta, horas[i], horas[i + 1]));
        }

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