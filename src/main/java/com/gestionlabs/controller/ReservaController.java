package com.gestionlabs.controller;

import com.gestionlabs.dto.ReservaDTO;
import com.gestionlabs.model.Reserva;
import com.gestionlabs.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Creación y gestión de reservas de laboratorio")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Operation(summary = "Crear reserva", description = "Crea una nueva reserva para un laboratorio en una franja horaria. Valida que la franja no esté ya ocupada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva creada exitosamente",
            content = @Content(schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "400", description = "Franja horaria ya ocupada u datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestBody ReservaDTO dto) {
        return ResponseEntity.ok(reservaService.crearReserva(dto));
    }

    @Operation(
        summary = "Mis reservas",
        description = "Retorna las reservas del usuario autenticado. El userId se extrae automáticamente del token Bearer en el header `Authorization`.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reservas del usuario autenticado",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Reserva.class)))),
        @ApiResponse(responseCode = "401", description = "Token ausente o inválido", content = @Content)
    })
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<Reserva>> misReservas(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        int dashIndex = token.indexOf('-');
        if (dashIndex < 0 || dashIndex == token.length() - 1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String[] parts = token.split("-", 6);
        if (parts.length < 6) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = parts[5];
        return ResponseEntity.ok(reservaService.obtenerReservasUsuario(userId));
    }

    @Operation(summary = "Reservas por usuario", description = "Retorna todas las reservas asociadas a un usuario por su ID.")
    @ApiResponse(responseCode = "200", description = "Lista de reservas",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Reserva.class))))
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> obtenerPorUsuario(
            @Parameter(description = "ID del usuario", required = true) @PathVariable String usuarioId) {
        return ResponseEntity.ok(reservaService.obtenerReservasUsuario(usuarioId));
    }

    @Operation(summary = "Reservas por laboratorio", description = "Retorna todas las reservas de un laboratorio específico.")
    @ApiResponse(responseCode = "200", description = "Lista de reservas",
        content = @Content(array = @ArraySchema(schema = @Schema(implementation = Reserva.class))))
    @GetMapping("/laboratorio/{labId}")
    public ResponseEntity<List<Reserva>> obtenerPorLaboratorio(
            @Parameter(description = "ID del laboratorio", required = true) @PathVariable String labId) {
        return ResponseEntity.ok(reservaService.obtenerReservasLaboratorio(labId));
    }

    @Operation(summary = "Cancelar reserva", description = "Cambia el estado de la reserva a `CANCELADA` sin eliminarla de la base de datos.")
    @ApiResponse(responseCode = "200", description = "Reserva cancelada", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable String id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar reserva físicamente", description = "Elimina la reserva de forma permanente de la base de datos.")
    @ApiResponse(responseCode = "204", description = "Reserva eliminada permanentemente", content = @Content)
    @DeleteMapping("/{id}/eliminar")
    public ResponseEntity<Void> eliminarFisico(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable String id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }
}