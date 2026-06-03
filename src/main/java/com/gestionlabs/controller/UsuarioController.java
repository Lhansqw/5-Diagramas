package com.gestionlabs.controller;

import com.gestionlabs.dto.UsuarioDTO;
import com.gestionlabs.model.Usuario;
import com.gestionlabs.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable String id) {
        return usuarioService.obtenerUsuarioPorId(id)
            .map(u -> ResponseEntity.ok(toDTO(u)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable String id, @RequestBody Usuario usuario) {
        try {
            Usuario updated = usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioDTO toDTO(Usuario u) {
        return new UsuarioDTO(
            u.getId(), u.getNombre(), u.getCorreo(), u.getRol(),
            u.getMatricula(), u.getCarrera(), u.getNivel()
        );
    }
}
