package com.gestionlabs.controller;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.model.Mantenimiento;
import com.gestionlabs.model.Reporte;
import com.gestionlabs.service.EquipoService;
import com.gestionlabs.service.MantenimientoService;
import com.gestionlabs.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administrador", description = "Operaciones exclusivas para usuarios con rol ADMINISTRADOR. Requieren Bearer token válido.")
@SecurityRequirement(name = "bearerAuth")
public class AdministradorController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private MantenimientoService mantenimientoService;

    private boolean esAdmin(String authHeader,
            com.gestionlabs.repository.UsuarioRepository usuarioRepository) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        String token = authHeader.substring(7);
        String[] parts = token.split("-");
        if (parts.length < 6) return false;
        String userId = parts[parts.length - 1];
        return usuarioRepository.findById(userId)
            .map(u -> "ADMINISTRADOR".equals(u.getRol()))
            .orElse(false);
    }

    @Autowired
    private com.gestionlabs.repository.UsuarioRepository usuarioRepository;

    @Operation(
        summary = "Generar reporte",
        description = "Genera un reporte con estadísticas globales: total de reservas, equipos, estados, etc. Solo accesible por administradores."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte generado",
            content = @Content(schema = @Schema(implementation = Reporte.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el token no corresponde a un administrador", content = @Content)
    })
    @GetMapping("/reportes")
    public ResponseEntity<Reporte> generarReporte(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!esAdmin(authHeader, usuarioRepository)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(reporteService.generarReporte());
    }

    @Operation(
        summary = "Registrar equipo (admin)",
        description = "Registra un nuevo equipo en el sistema. Opcionalmente puede asociarlo a un laboratorio mediante `laboratorioId`."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipo registrado",
            content = @Content(schema = @Schema(implementation = Equipo.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @PostMapping("/equipos")
    public ResponseEntity<Equipo> registrarEquipo(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Equipo equipo,
            @Parameter(description = "ID del laboratorio al que asociar el equipo") @RequestParam(required = false) String laboratorioId) {
        if (!esAdmin(authHeader, usuarioRepository)) {
            return ResponseEntity.status(403).build();
        }
        try {
            return ResponseEntity.ok(equipoService.registrarEquipo(equipo, laboratorioId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
        summary = "Programar mantenimiento",
        description = "Registra un mantenimiento para un equipo específico y cambia su estado a `MANTENIMIENTO`."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mantenimiento programado",
            content = @Content(schema = @Schema(implementation = Mantenimiento.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Equipo no encontrado", content = @Content)
    })
    @PutMapping("/equipos/{id}/mantenimiento")
    public ResponseEntity<Mantenimiento> programarMantenimiento(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Parameter(description = "ID del equipo", required = true) @PathVariable String id,
            @RequestBody Mantenimiento mantenimiento) {
        if (!esAdmin(authHeader, usuarioRepository)) {
            return ResponseEntity.status(403).build();
        }
        try {
            return ResponseEntity.ok(mantenimientoService.programarMantenimiento(id, mantenimiento));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}