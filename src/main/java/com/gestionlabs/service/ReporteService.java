package com.gestionlabs.service;

import com.gestionlabs.model.Reporte;
import com.gestionlabs.repository.ReservaRepository;
import com.gestionlabs.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class ReporteService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    public Reporte generarReporte() {
        Reporte reporte = new Reporte();
        reporte.setId(java.util.UUID.randomUUID().toString());
        reporte.setFechaGeneracion(new Date());
        
        long totalReservas = reservaRepository.count();
        reporte.setTotalReservas(totalReservas);
        
        long pendientes = reservaRepository.findAll().stream().filter(r -> "PENDIENTE".equals(r.getEstado())).count();
        reporte.setReservasPendientes(pendientes);

        long confirmadas = reservaRepository.findAll().stream().filter(r -> "CONFIRMADA".equals(r.getEstado())).count();
        reporte.setReservasConfirmadas(confirmadas);

        long canceladas = reservaRepository.findAll().stream().filter(r -> "CANCELADA".equals(r.getEstado())).count();
        reporte.setReservasCanceladas(canceladas);

        long totalEquipos = equipoRepository.count();
        reporte.setTotalEquipos(totalEquipos);

        long mantenimiento = equipoRepository.findAll().stream().filter(e -> "MANTENIMIENTO".equals(e.getEstado())).count();
        reporte.setEquiposEnMantenimiento(mantenimiento);

        return reporte;
    }
}
