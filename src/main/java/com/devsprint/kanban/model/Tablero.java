package com.devsprint.kanban.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "tableros")
@Data
@NoArgsConstructor
public class Tablero {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String propietarioId;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private List<Columna> columnas = new ArrayList<>();
}
