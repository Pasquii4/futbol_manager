package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jugadores")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private com.politecnics.football.repository.EquipoRepository equipoRepository; // Should already be there or not needed if jugador has team loaded

    @GetMapping
    public ResponseEntity<java.util.List<PlayerDTO>> getJugadores(@RequestParam(required = false) String teamId) {
        java.util.List<Jugador> jugadores;
        if (teamId != null) {
            jugadores = jugadorRepository.findByTeam_TeamId(teamId);
        } else {
            jugadores = jugadorRepository.findAll();
        }
        
        java.util.List<PlayerDTO> dtos = jugadores.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerDTO> getJugador(@PathVariable Long playerId) {
        return jugadorRepository.findById(playerId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private PlayerDTO convertToDTO(Jugador jugador) {
        return PlayerDTO.builder()
                .id(jugador.getId())
                .playerId(jugador.getJugadorId())
                .name(jugador.getNombre())
                .position(jugador.getPosicion() != null ? jugador.getPosicion().name() : null)
                .age((java.time.Period.between(jugador.getFechaNacimiento(), java.time.LocalDate.now()).getYears()))
                .overall(jugador.getCalidad() != null ? jugador.getCalidad().intValue() : 0)
                .potential(jugador.getCalidad() != null ? jugador.getCalidad().intValue() : 0)
                .goalsScored(jugador.getGoalsScored())
                .assists(jugador.getAssists())
                .yellowCards(jugador.getYellowCards())
                .redCards(jugador.getRedCards())
                .matchesPlayed(jugador.getMatchesPlayed())
                .teamId(jugador.getTeam() != null ? jugador.getTeam().getTeamId() : null)
                .teamName(jugador.getTeam() != null ? jugador.getTeam().getNombre() : null)
                .build();
    }
}
