package com.gestionlabs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reportes")
public class Reporte {
    @Id
    private String id;
    private Date fechaGeneracion;
    private long totalReservas;
    private long reservasConfirmadas;
    private long reservasCanceladas;
    private long reservasPendientes;
    private long totalEquipos;
    private long equiposEnMantenimiento;
}
