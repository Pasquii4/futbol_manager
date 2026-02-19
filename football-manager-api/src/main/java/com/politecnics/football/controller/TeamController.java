package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.dto.TeamDTO;
import com.politecnics.football.entity.Player;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.TeamRepository;
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
    private TeamRepository teamRepository;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        List<TeamDTO> dtos = teams.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDTO> getTeam(@PathVariable String teamId) {
        return teamRepository.findByTeamId(teamId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private TeamDTO convertToDTO(Team team) {
        List<PlayerDTO> playerDTOs = team.getPlayers() != null ? 
                team.getPlayers().stream().map(this::convertPlayerToDTO).collect(Collectors.toList()) :
                List.of();
                
        return TeamDTO.builder()
                .id(team.getId())
                .teamId(team.getTeamId())
                .name(team.getName())
                .stadium(team.getStadium())
                .budget(team.getBudget())
                .overallRating(team.getOverallRating())
                .formation(team.getFormation())
                .mentality(team.getMentality())
                .players(playerDTOs)
                .build();
    }
    
    private PlayerDTO convertPlayerToDTO(Player player) {
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
                .matchesPlayed(player.getMatchesPlayed())
                .marketValue(player.getMarketValue())
                .build();
    }

    @PutMapping("/{teamId}/tactics")
    public ResponseEntity<TeamDTO> updateTactics(@PathVariable String teamId, @RequestParam String formation, @RequestParam String mentality) {
        return teamRepository.findByTeamId(teamId)
                .map(team -> {
                    team.setFormation(formation);
                    team.setMentality(mentality);
                    return teamRepository.save(team);
                })
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
