package com.gestionlabs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    private String id;
    private String nombre;
    private String estado;
    private String laboratorioId;
}
