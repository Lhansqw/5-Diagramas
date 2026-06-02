package com.devsprint.kanban.dto;

import java.util.List;

public record TareaDTO(
    String titulo,
    String descripcion,
    String prioridad,
    List<String> etiquetas,
    String asignadoA,
    String fechaVencimiento
) {}
