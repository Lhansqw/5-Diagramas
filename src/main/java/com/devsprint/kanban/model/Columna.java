package com.devsprint.kanban.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Columna {
    private String id;
    private String nombre;
    private int orden;
    private Integer limiteWIP;
    private List<Tarea> tareas = new ArrayList<>();

    public Columna(String id, String nombre, int orden, Integer limiteWIP) {
        this.id = id;
        this.nombre = nombre;
        this.orden = orden;
        this.limiteWIP = limiteWIP;
        this.tareas = new ArrayList<>();
    }
}
