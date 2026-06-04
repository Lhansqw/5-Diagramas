package com.gestionlabs.controller;

import com.gestionlabs.dto.AuthRequest;
import com.gestionlabs.dto.AuthResponse;
import com.gestionlabs.model.Usuario;
import com.gestionlabs.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para login y registro de usuarios")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica al usuario con correo y contraseña. Retorna un token en formato `<uuid>-<userId>` junto con los datos del usuario."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    @Operation(
        summary = "Registrar usuario",
        description = "Crea un nuevo usuario en el sistema. El rol por defecto es ESTUDIANTE si no se especifica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente",
            content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya existente", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(authService.register(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}