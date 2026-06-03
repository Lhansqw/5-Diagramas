package com.gestionlabs.service;

import com.gestionlabs.dto.AuthRequest;
import com.gestionlabs.dto.AuthResponse;
import com.gestionlabs.model.Usuario;
import com.gestionlabs.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AuthResponse login(AuthRequest request) {
        Optional<Usuario> userOpt = usuarioRepository.findByCorreo(request.getCorreo());
        
        if (userOpt.isPresent() && userOpt.get().getContraseña().equals(request.getContraseña())) {
            Usuario user = userOpt.get();
            // Simulación de JWT generando un UUID
            String fakeToken = UUID.randomUUID().toString() + "-" + user.getId();
            return new AuthResponse(fakeToken, user.getId(), user.getNombre(), user.getRol());
        }
        throw new RuntimeException("Credenciales inválidas");
    }

    public Usuario register(Usuario usuario) {
        Optional<Usuario> existing = usuarioRepository.findByCorreo(usuario.getCorreo());
        if (existing.isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (usuario.getRol() == null) {
            usuario.setRol("ESTUDIANTE");
        }
        return usuarioRepository.save(usuario);
    }
}
