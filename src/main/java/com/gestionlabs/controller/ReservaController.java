package com.gestionlabs.controller;

import com.gestionlabs.dto.ReservaDTO;
import com.gestionlabs.model.Reserva;
import com.gestionlabs.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
