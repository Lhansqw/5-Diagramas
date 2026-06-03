package com.gestionlabs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import com.gestionlabs.repository.UsuarioRepository;
import com.gestionlabs.model.Usuario;
import com.gestionlabs.repository.LaboratorioRepository;
import com.gestionlabs.model.Laboratorio;
import com.gestionlabs.model.Equipo;
import java.util.Arrays;

@SpringBootApplication
public class GestionLabsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionLabsApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository userRepo, LaboratorioRepository labRepo) {
		return args -> {
			try {
				if (userRepo.count() == 0) {
					Usuario admin = new Usuario();
					admin.setNombre("Administrador Principal");
					admin.setCorreo("admin@gestionlabs.edu");
					admin.setContraseña("admin123");
					admin.setRol("ADMINISTRADOR");
					userRepo.save(admin);

					Usuario estudiante = new Usuario();
					estudiante.setNombre("Juan Pérez");
					estudiante.setCorreo("juan.perez@estudiante.edu");
					estudiante.setContraseña("estudiante123");
					estudiante.setRol("ESTUDIANTE");
					estudiante.setMatricula("20250001");
					estudiante.setCarrera("Ingeniería de Software");
					userRepo.save(estudiante);

					System.out.println(">>> Usuarios de prueba creados en MongoDB.");
				}

				if (labRepo.count() == 0) {
					Laboratorio lab = new Laboratorio();
					lab.setNombre("Laboratorio de Redes");
					
					Equipo eq1 = new Equipo("eq-1", "Router Cisco 2901", "DISPONIBLE");
					Equipo eq2 = new Equipo("eq-2", "Switch Catalyst 2960", "MANTENIMIENTO");
					
					lab.setEquipos(Arrays.asList(eq1, eq2));
					labRepo.save(lab);

					Laboratorio lab2 = new Laboratorio();
					lab2.setNombre("Laboratorio de Física");
					
					Equipo eq3 = new Equipo("eq-3", "Microscopio Electrónico", "DISPONIBLE");
					
					lab2.setEquipos(Arrays.asList(eq3));
					labRepo.save(lab2);

					System.out.println(">>> Laboratorios de prueba creados en MongoDB.");
				}
			} catch (Exception e) {
				System.err.println(">>> ADVERTENCIA: No se pudo conectar a MongoDB. " + e.getMessage());
			}
		};
	}
}
