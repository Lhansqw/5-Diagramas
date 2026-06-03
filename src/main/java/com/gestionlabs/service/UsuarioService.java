package com.gestionlabs.service;

import com.gestionlabs.model.Usuario;
import com.gestionlabs.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(String id, Usuario det) {
        return usuarioRepository.findById(id).map(user -> {
            user.setNombre(det.getNombre());
            user.setCorreo(det.getCorreo());
            if (det.getContraseña() != null && !det.getContraseña().isEmpty()) {
                user.setContraseña(det.getContraseña());
            }
            user.setRol(det.getRol());
            user.setMatricula(det.getMatricula());
            user.setCarrera(det.getCarrera());
            user.setNivel(det.getNivel());
            return usuarioRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminarUsuario(String id) {
        usuarioRepository.deleteById(id);
    }
}
