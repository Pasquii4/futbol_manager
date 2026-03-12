package com.politecnics.football.controller;

import com.politecnics.football.dto.MatchDTO;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.repository.MatchRepository;
import com.politecnics.football.entity.MatchStats;
import com.politecnics.football.repository.MatchEventRepository;
import com.politecnics.football.repository.MatchStatsRepository;
import com.politecnics.football.dto.MatchEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchEventRepository matchEventRepository;

    @Autowired
    private MatchStatsRepository matchStatsRepository;

    @GetMapping
    public ResponseEntity<List<MatchDTO>> getMatches(@RequestParam(required = false) Integer matchday) {
        List<Match> matches;
        if (matchday != null) {
            matches = matchRepository.findByMatchday(matchday);
        } else {
            matches = matchRepository.findAllByOrderByMatchdayAsc();
        }
        
        List<MatchDTO> dtos = matches.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatch(@PathVariable Long id) {
        return matchRepository.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/live-events")
    public ResponseEntity<List<MatchEventDTO>> getMatchEvents(@PathVariable Long id) {
        List<MatchEvent> events = matchEventRepository.findByMatchId(id);
        events.sort((a, b) -> a.getMinute().compareTo(b.getMinute()));
        
        List<MatchEventDTO> dtos = events.stream().map((MatchEvent e) -> MatchEventDTO.builder()
                .id(e.getId())
                .playerId(e.getJugador() != null ? e.getJugador().getId() : null)
                .playerName(e.getJugador() != null ? e.getJugador().getNombre() : "Unknown")
                .type(e.getType().name())
                .minute(e.getMinute())
                .teamId(e.getJugador() != null && e.getJugador().getTeam() != null ? e.getJugador().getTeam().getId() : null)
                .build()).collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<MatchStats> getMatchStats(@PathVariable Long id) {
        MatchStats stats = matchStatsRepository.findByMatchId(id);
        if (stats == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(stats);
    }

    private MatchDTO convertToDTO(Match match) {
        return MatchDTO.builder()
                .id(match.getId())
                .matchday(match.getMatchday())
                .homeTeamId(match.getHomeTeam() != null ? match.getHomeTeam().getId() : null)
                .homeTeamName(match.getHomeTeam() != null ? match.getHomeTeam().getNombre() : "Unknown")
                .awayTeamId(match.getAwayTeam() != null ? match.getAwayTeam().getId() : null)
                .awayTeamName(match.getAwayTeam() != null ? match.getAwayTeam().getNombre() : "Unknown")
                .homeGoals(match.getHomeGoals())
                .awayGoals(match.getAwayGoals())
                .played(match.isPlayed())
                .matchDate(match.getMatchDate())
                .events(match.getEvents() != null ? match.getEvents().stream().map(e -> MatchEventDTO.builder()
                        .id(e.getId())
                        .playerId(e.getJugador() != null ? e.getJugador().getId() : null)
                        .playerName(e.getJugador() != null ? e.getJugador().getNombre() : "Unknown")
                        .type(e.getType().name())
                        .minute(e.getMinute())
                        .teamId(e.getJugador() != null && e.getJugador().getTeam() != null ? e.getJugador().getTeam().getId() : null)
                        .build()).collect(Collectors.toList()) : new ArrayList<MatchEventDTO>())
                .build();
    }
}
