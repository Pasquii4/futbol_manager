package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private JugadorRepository jugadorRepository;

    @GetMapping("/top-scorers")
    public ResponseEntity<List<PlayerDTO>> getTopScorers() {
        return ResponseEntity.ok(jugadorRepository.findTop20ByOrderByGoalsScoredDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/top-assists")
    public ResponseEntity<List<PlayerDTO>> getTopAssists() {
        return ResponseEntity.ok(jugadorRepository.findTop20ByOrderByAssistsDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<PlayerDTO>> getTopRated() {
        // Assuming Overall as rating for now, or averageRating if implemented
        return ResponseEntity.ok(jugadorRepository.findTop20ByOrderByCalidadDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
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
                .marketValue(jugador.getMarketValue())
                .teamId(jugador.getTeam() != null ? jugador.getTeam().getTeamId() : null)
                .teamName(jugador.getTeam() != null ? jugador.getTeam().getNombre() : null)
                .build();
    }
}
