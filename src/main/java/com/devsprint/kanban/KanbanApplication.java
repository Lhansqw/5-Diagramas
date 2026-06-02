package com.devsprint.kanban;

import com.devsprint.kanban.model.Columna;
import com.devsprint.kanban.model.Tablero;
import com.devsprint.kanban.repository.TableroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class KanbanApplication {

	public static void main(String[] args) {
		SpringApplication.run(KanbanApplication.class, args);
	}

	@Bean
	CommandLineRunner init(TableroRepository repo) {
		return args -> {
			try {
				if (repo.count() == 0) {
					Tablero tablero = new Tablero();
					tablero.setNombre("Mi Tablero DevSprint");
					tablero.setDescripcion("Tablero de ejemplo para el proyecto universitario");
					tablero.setPropietarioId("estudiante-01");

					Columna porHacer = new Columna("col-1", "Por Hacer", 0, 10);
					Columna enProceso = new Columna("col-2", "En Proceso", 1, 3);
					Columna revision = new Columna("col-3", "En Revisión", 2, 5);
					Columna hecho = new Columna("col-4", "Hecho", 3, null);

					tablero.setColumnas(new ArrayList<>(Arrays.asList(porHacer, enProceso, revision, hecho)));
					repo.save(tablero);
					System.out.println(">>> Tablero de ejemplo creado en MongoDB.");
				}
			} catch (Exception e) {
				System.err.println(">>> ADVERTENCIA: No se pudo conectar a MongoDB. Inicia MongoDB e reinicia la app.");
				System.err.println(">>> " + e.getMessage());
			}
		};
	}
}
