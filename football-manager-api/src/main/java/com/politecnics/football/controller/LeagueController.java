package com.politecnics.football.controller;

import com.politecnics.football.dto.LeagueStatusDTO;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Player;
import com.politecnics.football.repository.MatchRepository;
import com.politecnics.football.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/league")
@CrossOrigin(origins = "*")
public class LeagueController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping("/status")
    public ResponseEntity<LeagueStatusDTO> getLeagueStatus() {
        List<Match> matches = matchRepository.findAll();
        
        if (matches.isEmpty()) {
            return ResponseEntity.ok(LeagueStatusDTO.builder()
                    .currentMatchday(0)
                    .totalMatchdays(0)
                    .seasonYear(2025)
                    .isStarted(false)
                    .isFinished(false)
                    .build());
        }

        int totalMatchdays = matches.stream()
                .map(Match::getMatchday)
                .max(Integer::compareTo)
                .orElse(38);

        // Find first matchday that is NOT fully played
        int currentMatchday = matches.stream()
                .filter(m -> !m.isPlayed())
                .map(Match::getMatchday)
                .min(Integer::compareTo)
                .orElse(totalMatchdays + 1); // All played

        // If current > total, it's finished
        boolean isFinished = currentMatchday > totalMatchdays;
        if (isFinished) currentMatchday = totalMatchdays; // cap at last for display or keep as is? 
        // Logic: if all played, we are at "End of Season".

        return ResponseEntity.ok(LeagueStatusDTO.builder()
                .currentMatchday(isFinished ? totalMatchdays : currentMatchday)
                .totalMatchdays(totalMatchdays)
                .seasonYear(2025)
                .isStarted(true)
                .isFinished(isFinished)
                .build());
    }

    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<String> resetLeague() {
        // 1. Reset Matches
        List<Match> matches = matchRepository.findAll();
        for (Match match : matches) {
            match.setPlayed(false);
            match.setHomeGoals(0);
            match.setAwayGoals(0);
            match.setHomeGoals(null); // Or 0? entity has Integer
            match.setAwayGoals(null);
        }
        matchRepository.saveAll(matches);

        // 2. Reset Players Stats
        List<Player> players = playerRepository.findAll();
        for (Player player : players) {
            player.setGoalsScored(0);
            player.setAssists(0);
            player.setYellowCards(0);
            player.setRedCards(0);
            player.setMatchesPlayed(0);
        }
        playerRepository.saveAll(players);

        return ResponseEntity.ok("League reset successfully");
    }
}
