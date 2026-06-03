package com.gestionlabs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioDTO {
    private String id;
    private String nombre;
    private List<EquipoDTO> equipos;
}
