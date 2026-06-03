package com.gestionlabs.controller;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.model.Mantenimiento;
import com.gestionlabs.model.Reporte;
import com.gestionlabs.service.EquipoService;
import com.gestionlabs.service.MantenimientoService;
import com.gestionlabs.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdministradorController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private MantenimientoService mantenimientoService;

    /**
     * Verifica que el rol del token Bearer sea ADMINISTRADOR.
     * Token format: "<uuid>-<userId>" (generado en AuthService).
     * El rol se valida consultando el usuario por ID extraído del token.
     */
    private boolean esAdmin(String authHeader,
            com.gestionlabs.repository.UsuarioRepository usuarioRepository) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        String token = authHeader.substring(7);
        String[] parts = token.split("-");
        if (parts.length < 6) return false;
        // El userId está al final: formato uuid (5 partes) + "-" + userId
        String userId = parts[parts.length - 1];
        return usuarioRepository.findById(userId)
            .map(u -> "ADMINISTRADOR".equals(u.getRol()))
            .orElse(false);
    }

    @Autowired
    private com.gestionlabs.repository.UsuarioRepository usuarioRepository;

    // ─── GET /api/admin/reportes ─────────────────────
    @GetMapping("/reportes")
    public ResponseEntity<Reporte> generarReporte(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!esAdmin(authHeader, usuarioRepository)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(reporteService.generarReporte());
    }

    // ─── POST /api/admin/equipos ──────────────────────
    @PostMapping("/equipos")
    public ResponseEntity<Equipo> registrarEquipo(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Equipo equipo,
            @RequestParam(required = false) String laboratorioId) {
        if (!esAdmin(authHeader, usuarioRepository)) {
            return ResponseEntity.status(403).build();
        }
        try {
            return ResponseEntity.ok(equipoService.registrarEquipo(equipo, laboratorioId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ─── PUT /api/admin/equipos/{id}/mantenimiento ────
    @PutMapping("/equipos/{id}/mantenimiento")
    public ResponseEntity<Mantenimiento> programarMantenimiento(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable String id,
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
