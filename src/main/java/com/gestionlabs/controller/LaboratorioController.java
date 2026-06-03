package com.gestionlabs.controller;

import com.gestionlabs.model.Equipo;
import com.gestionlabs.model.Laboratorio;
import com.gestionlabs.service.LaboratorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioController {

    @Autowired
    private LaboratorioService laboratorioService;

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
}
