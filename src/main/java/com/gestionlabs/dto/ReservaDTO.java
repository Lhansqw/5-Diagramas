package com.gestionlabs.dto;

import com.gestionlabs.model.FranjaHoraria;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private String usuarioId;
    private String laboratorioId;
    private String equipoId;
    private FranjaHoraria franjaHoraria;
}
