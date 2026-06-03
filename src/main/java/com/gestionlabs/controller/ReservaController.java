package com.gestionlabs.controller;

import com.gestionlabs.dto.ReservaDTO;
import com.gestionlabs.model.Reserva;
import com.gestionlabs.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody ReservaDTO dto) {
        return ResponseEntity.ok(reservaService.crearReserva(dto));
    }

    /**
     * GET /api/reservas/mis-reservas
     * Header: Authorization: Bearer <uuid>-<userId>
     * Extrae el userId del token y devuelve sus reservas.
     */
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<Reserva>> misReservas(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Token format: "<uuid>-<userId>"  (AuthService uses UUID.randomUUID() + "-" + user.getId())
        String token = authHeader.substring(7); // strip "Bearer "
        int dashIndex = token.indexOf('-');
        if (dashIndex < 0 || dashIndex == token.length() - 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // UUID has exactly 4 dashes; userId starts after the 5th dash
        // Split on dash limit 6 to get [time, low, mid, hi, userId] safely
        String[] parts = token.split("-", 6);
        if (parts.length < 6) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = parts[5];
        return ResponseEntity.ok(reservaService.obtenerReservasUsuario(userId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> obtenerPorUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(reservaService.obtenerReservasUsuario(usuarioId));
    }

    @GetMapping("/laboratorio/{labId}")
    public ResponseEntity<List<Reserva>> obtenerPorLaboratorio(@PathVariable String labId) {
        return ResponseEntity.ok(reservaService.obtenerReservasLaboratorio(labId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable String id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/eliminar")
    public ResponseEntity<Void> eliminarFisico(@PathVariable String id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }
}
