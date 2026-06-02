package com.devsprint.kanban.controller;

import com.devsprint.kanban.dto.MoverTareaDTO;
import com.devsprint.kanban.dto.TareaDTO;
import com.devsprint.kanban.model.Tablero;
import com.devsprint.kanban.model.Tarea;
import com.devsprint.kanban.service.TableroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tableros")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TableroController {

    private final TableroService service;

    // GET /api/tableros — listar todos los tableros
    @GetMapping
    public ResponseEntity<List<Tablero>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // GET /api/tableros/{id} — obtener un tablero completo con columnas y tareas
    @GetMapping("/{id}")
    public ResponseEntity<Tablero> obtener(@PathVariable String id) {
        return ResponseEntity.ok(service.obtenerTablero(id));
    }

    // POST /api/tableros — crear un tablero nuevo
    @PostMapping
    public ResponseEntity<Tablero> crear(@RequestBody Map<String, String> body) {
        Tablero nuevo = service.crearTablero(
            body.get("nombre"),
            body.get("descripcion"),
            body.getOrDefault("propietarioId", "anonimo")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // POST /api/tableros/{tableroId}/columnas/{columnaId}/tareas — agregar tarea a columna
    @PostMapping("/{tableroId}/columnas/{columnaId}/tareas")
    public ResponseEntity<Tarea> agregarTarea(
            @PathVariable String tableroId,
            @PathVariable String columnaId,
            @RequestBody TareaDTO dto) {
        Tarea tarea = service.crearTarea(tableroId, columnaId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarea);
    }

    // PUT /api/tableros/{tableroId}/tareas/{tareaId}/mover — mover tarea entre columnas
    @PutMapping("/{tableroId}/tareas/{tareaId}/mover")
    public ResponseEntity<Tablero> moverTarea(
            @PathVariable String tableroId,
            @PathVariable String tareaId,
            @RequestBody MoverTareaDTO dto) {
        return ResponseEntity.ok(service.moverTarea(tableroId, tareaId, dto));
    }

    // DELETE /api/tableros/{tableroId}/tareas/{tareaId} — eliminar tarea
    @DeleteMapping("/{tableroId}/tareas/{tareaId}")
    public ResponseEntity<Tablero> eliminarTarea(
            @PathVariable String tableroId,
            @PathVariable String tareaId) {
        return ResponseEntity.ok(service.eliminarTarea(tableroId, tareaId));
    }

    // Manejador de errores global para esta clase
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
