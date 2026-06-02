package com.devsprint.kanban.service;

import com.devsprint.kanban.dto.MoverTareaDTO;
import com.devsprint.kanban.dto.TareaDTO;
import com.devsprint.kanban.model.Columna;
import com.devsprint.kanban.model.Tablero;
import com.devsprint.kanban.model.Tarea;
import com.devsprint.kanban.repository.TableroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TableroService {

    private final TableroRepository repo;

    public List<Tablero> listarTodos() {
        return repo.findAll();
    }

    public Tablero obtenerTablero(String tableroId) {
        return repo.findById(tableroId)
                .orElseThrow(() -> new RuntimeException("Tablero no encontrado: " + tableroId));
    }

    public Tablero crearTablero(String nombre, String descripcion, String propietarioId) {
        Tablero t = new Tablero();
        t.setNombre(nombre);
        t.setDescripcion(descripcion);
        t.setPropietarioId(propietarioId);
        return repo.save(t);
    }

    public Tarea crearTarea(String tableroId, String columnaId, TareaDTO dto) {
        Tablero tablero = obtenerTablero(tableroId);

        Columna columna = tablero.getColumnas().stream()
                .filter(c -> c.getId().equals(columnaId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Columna no encontrada: " + columnaId));

        // Validar límite WIP
        if (columna.getLimiteWIP() != null && columna.getTareas().size() >= columna.getLimiteWIP()) {
            throw new RuntimeException("Límite WIP alcanzado para la columna: " + columna.getNombre());
        }

        Tarea tarea = new Tarea();
        tarea.setId(UUID.randomUUID().toString());
        tarea.setTitulo(dto.titulo());
        tarea.setDescripcion(dto.descripcion());
        tarea.setPrioridad(dto.prioridad() != null ? dto.prioridad() : "MEDIA");
        tarea.setEtiquetas(dto.etiquetas() != null ? dto.etiquetas() : List.of());
        tarea.setAsignadoA(dto.asignadoA());
        tarea.setFechaVencimiento(dto.fechaVencimiento());
        tarea.setEstado(columna.getNombre());

        columna.getTareas().add(tarea);
        repo.save(tablero);
        return tarea;
    }

    public Tablero moverTarea(String tableroId, String tareaId, MoverTareaDTO dto) {
        Tablero tablero = obtenerTablero(tableroId);

        Tarea tarea = null;
        Columna origen = null;

        // Buscar tarea en todas las columnas
        for (Columna col : tablero.getColumnas()) {
            for (Tarea t : col.getTareas()) {
                if (t.getId().equals(tareaId)) {
                    tarea = t;
                    origen = col;
                    break;
                }
            }
            if (tarea != null) break;
        }

        if (tarea == null) throw new RuntimeException("Tarea no encontrada: " + tareaId);

        Columna destino = tablero.getColumnas().stream()
                .filter(c -> c.getId().equals(dto.columnaDestinoId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Columna destino no encontrada: " + dto.columnaDestinoId()));

        // Validar límite WIP en destino
        if (destino.getLimiteWIP() != null && destino.getTareas().size() >= destino.getLimiteWIP()) {
            throw new RuntimeException("Límite WIP alcanzado en columna destino: " + destino.getNombre());
        }

        origen.getTareas().remove(tarea);
        tarea.setEstado(destino.getNombre());
        destino.getTareas().add(tarea);

        return repo.save(tablero);
    }

    public Tablero eliminarTarea(String tableroId, String tareaId) {
        Tablero tablero = obtenerTablero(tableroId);
        tablero.getColumnas().forEach(col ->
                col.getTareas().removeIf(t -> t.getId().equals(tareaId))
        );
        return repo.save(tablero);
    }
}
