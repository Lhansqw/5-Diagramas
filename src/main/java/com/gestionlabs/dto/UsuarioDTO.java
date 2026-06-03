package com.gestionlabs.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private String id;
    private String nombre;
    private String correo;
    private String rol;
    private String matricula;
    private String carrera;
    private String nivel;
}
