package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.entity.Player;
import com.politecnics.football.repository.PlayerRepository;
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
    private PlayerRepository playerRepository;

    @GetMapping("/top-scorers")
    public ResponseEntity<List<PlayerDTO>> getTopScorers() {
        return ResponseEntity.ok(playerRepository.findTop20ByOrderByGoalsScoredDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/top-assists")
    public ResponseEntity<List<PlayerDTO>> getTopAssists() {
        return ResponseEntity.ok(playerRepository.findTop20ByOrderByAssistsDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<PlayerDTO>> getTopRated() {
        // Assuming Overall as rating for now, or averageRating if implemented
        return ResponseEntity.ok(playerRepository.findTop20ByOrderByOverallDesc()
                .stream().map(this::convertToDTO).collect(Collectors.toList()));
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
