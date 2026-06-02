package com.devsprint.kanban.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {
    private String id;
    private String titulo;
    private String descripcion;
    private String estado;       // POR_HACER, EN_PROCESO, EN_REVISION, HECHO
    private String prioridad;    // ALTA, MEDIA, BAJA
    private List<String> etiquetas = new ArrayList<>();
    private String asignadoA;
    private String fechaVencimiento;
}
