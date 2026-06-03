package com.gestionlabs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;
    private String nombre;
    private String correo;
    private String contraseña;
    private String rol; // "ESTUDIANTE" o "ADMINISTRADOR"

    // Campos de Estudiante
    private String matricula;
    private String carrera;

    // Campos de Administrador
    private String nivel;
}
