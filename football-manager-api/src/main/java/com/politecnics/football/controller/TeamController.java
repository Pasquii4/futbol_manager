package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.dto.TeamDTO;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.repository.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private EquipoRepository equipoRepository;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<Equipo> teams = equipoRepository.findAll();
        List<TeamDTO> dtos = teams.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable String teamId) {
        return equipoRepository.findByTeamId(teamId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private TeamDTO convertToDTO(Equipo team) {
        List<PlayerDTO> playerDTOs = team.getJugadores() != null ? 
                team.getJugadores().stream().map(this::convertPlayerToDTO).collect(Collectors.toList()) :
                List.of();
                
        return TeamDTO.builder()
                .id(team.getId())
                .teamId(team.getTeamId())
                .name(team.getNombre())
                .stadium(team.getStadium())
                .budget(team.getBudget())
                .overallRating(team.getCalidadRating())
                .formation(team.getFormation())
                .mentality(team.getMentality())
                .jugadores(playerDTOs)
                .build();
    }
    
    private PlayerDTO convertPlayerToDTO(Jugador jugador) {
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
                .matchesPlayed(jugador.getMatchesPlayed())
                .marketValue(jugador.getMarketValue())
                .build();
    }

    @PutMapping("/{teamId}/tactics")
    public ResponseEntity<TeamDTO> updateTactics(@PathVariable String teamId, @RequestParam String formation, @RequestParam String mentality) {
        return equipoRepository.findByTeamId(teamId)
                .map(team -> {
                    // team.setFormation(formation); // Requires mapping to Tactica entity
                    // team.setMentality(mentality); // Requires mapping to Tactica entity
                    return equipoRepository.save(team);
                })
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
