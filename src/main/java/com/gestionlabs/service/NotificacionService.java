package com.gestionlabs.service;

import com.gestionlabs.model.Notificacion;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificacionService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);

    public Notificacion enviarNotificacion(String usuarioId, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(UUID.randomUUID().toString());
        notificacion.setUsuarioId(usuarioId);
        notificacion.setMensaje(mensaje);
        notificacion.setFechaEnvio(new Date());
        notificacion.setLeido(false);

        logger.info("[SMTP Mail Sender] Enviando correo a usuario: {} - Mensaje: {}", usuarioId, mensaje);

        return notificacion;
    }
}
