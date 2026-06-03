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
@Document(collection = "reservas")
public class Reserva {
    @Id
    private String id;
    private String estado; // "PENDIENTE", "CONFIRMADA", "CANCELADA"
    private String usuarioId;
    private String laboratorioId;
    private String equipoId; // Opcional, si reserva un equipo específico
    private FranjaHoraria franjaHoraria;
    private Date fechaReserva;
}
