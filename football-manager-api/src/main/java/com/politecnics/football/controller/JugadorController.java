package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.service.JugadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
@RequiredArgsConstructor
@Tag(name = "Jugadores", description = "Operaciones sobre jugadores")
public class JugadorController {
    private final JugadorService jugadorService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un jugador")
    public ResponseEntity<PlayerDTO> getJugadorById(@PathVariable Long id) {
        return ResponseEntity.ok(jugadorService.getJugadorById(id));
    }
    
    @GetMapping("/equipo/{equipoId}")
    @Operation(summary = "Obtener jugadores de un equipo")
    public ResponseEntity<List<PlayerDTO>> getJugadoresByEquipo(@PathVariable Long equipoId) {
        return ResponseEntity.ok(jugadorService.getJugadoresByEquipo(equipoId));
    }
    
    @GetMapping("/mercado")
    @Operation(summary = "Obtener jugadores libres (mercado)")
    public ResponseEntity<List<PlayerDTO>> getMercadoFichajes() {
        return ResponseEntity.ok(jugadorService.getMercadoFichajes());
    }
}
