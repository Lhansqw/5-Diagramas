package com.gestionlabs.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FranjaHoraria {
    private String fecha;      // ej: "2026-06-03"
    private String horaInicio; // ej: "10:00"
    private String horaFin;    // ej: "12:00"
}
