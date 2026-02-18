package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.entity.Player;
import com.politecnics.football.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private com.politecnics.football.repository.TeamRepository teamRepository; // Should already be there or not needed if player has team loaded

    @GetMapping
    public ResponseEntity<java.util.List<PlayerDTO>> getPlayers(@RequestParam(required = false) String teamId) {
        java.util.List<Player> players;
        if (teamId != null) {
            players = playerRepository.findByTeam_TeamId(teamId);
        } else {
            players = playerRepository.findAll();
        }
        
        java.util.List<PlayerDTO> dtos = players.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long playerId) {
        return playerRepository.findById(playerId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private PlayerDTO convertToDTO(Player player) {
        return PlayerDTO.builder()
                .id(player.getId())
                .playerId(player.getPlayerId())
                .name(player.getName())
                .position(player.getPosition())
                .age(player.getAge())
                .overall(player.getOverall())
                .potential(player.getPotential())
                .goalsScored(player.getGoalsScored())
                .assists(player.getAssists())
                .yellowCards(player.getYellowCards())
                .redCards(player.getRedCards())
                .matchesPlayed(player.getMatchesPlayed())
                .teamId(player.getTeam() != null ? player.getTeam().getTeamId() : null)
                .teamName(player.getTeam() != null ? player.getTeam().getName() : null)
                .build();
    }
}
