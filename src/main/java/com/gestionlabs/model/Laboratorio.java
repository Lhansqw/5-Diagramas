package com.gestionlabs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "laboratorios")
public class Laboratorio {
    @Id
    private String id;
    private String nombre;
    private List<Equipo> equipos = new ArrayList<>();
}
