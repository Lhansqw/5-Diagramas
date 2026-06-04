package com.gestionlabs.controller;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
@Tag(name = "Equipos", description = "Gestión de equipos de laboratorio y su estado operativo")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @Operation(summary = "Listar todos los equipos")
    @ApiResponse(responseCode = "200", description = "Lista de equipos",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Equipo.class))))
    @GetMapping
    public ResponseEntity<List<Equipo>> listar() {
        return ResponseEntity.ok(equipoService.listarEquipos());
    }

    @Operation(summary = "Obtener equipo por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo encontrado",
            content = @Content(schema = @Schema(implementation = Equipo.class))),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerPorId(
            @Parameter(description = "ID del equipo", required = true) @PathVariable String id) {
        return equipoService.obtenerEquipoPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Registrar equipo",
        description = "Registra un nuevo equipo. Si se indica `laboratorioId`, el equipo queda asociado a ese laboratorio."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo registrado",
            content = @Content(schema = @Schema(implementation = Equipo.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Equipo> registrar(
            @RequestBody Equipo equipo,
            @Parameter(description = "ID del laboratorio al que se asociará el equipo") @RequestParam(required = false) String laboratorioId) {
        try {
            return ResponseEntity.ok(equipoService.registrarEquipo(equipo, laboratorioId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
        summary = "Actualizar estado del equipo",
        description = "Cambia el estado operativo del equipo. Valores válidos: `DISPONIBLE`, `EN_USO`, `MANTENIMIENTO`."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado",
            content = @Content(schema = @Schema(implementation = Equipo.class))),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Nuevo estado del equipo",
        content = @Content(
            schema = @Schema(type = "object"),
            examples = @ExampleObject(value = "{\"estado\": \"DISPONIBLE\"}")
        )
    )
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Equipo> actualizarEstado(
            @Parameter(description = "ID del equipo", required = true) @PathVariable String id,
            @RequestBody Map<String, String> body) {
        try {
            String estado = body.get("estado");
            return ResponseEntity.ok(equipoService.actualizarEstadoEquipo(id, estado));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}