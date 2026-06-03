package com.gestionlabs.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {
    private String id;
    private String nombre;
    private String estado; // "DISPONIBLE", "EN_USO", "MANTENIMIENTO"
    private List<Mantenimiento> mantenimientos = new ArrayList<>();

    public Equipo(String id, String nombre, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }
}
