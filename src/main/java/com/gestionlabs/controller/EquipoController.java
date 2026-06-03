package com.gestionlabs.controller;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping
    public ResponseEntity<List<Equipo>> listar() {
        return ResponseEntity.ok(equipoService.listarEquipos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerPorId(@PathVariable String id) {
        return equipoService.obtenerEquipoPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Equipo> registrar(
            @RequestBody Equipo equipo,
            @RequestParam(required = false) String laboratorioId) {
        try {
            return ResponseEntity.ok(equipoService.registrarEquipo(equipo, laboratorioId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Equipo> actualizarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        try {
            String estado = body.get("estado");
            return ResponseEntity.ok(equipoService.actualizarEstadoEquipo(id, estado));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
