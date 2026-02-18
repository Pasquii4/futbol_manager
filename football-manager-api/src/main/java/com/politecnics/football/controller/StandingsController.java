package com.politecnics.football.controller;

import com.politecnics.football.dto.StandingsDTO;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.MatchRepository;
import com.politecnics.football.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/standings")
@CrossOrigin(origins = "*")
public class StandingsController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping
    public ResponseEntity<List<StandingsDTO>> getStandings() {
        List<Team> teams = teamRepository.findAll();
        List<Match> matches = matchRepository.findByPlayed(true);

        Map<Long, StandingsDTO> standingsMap = new HashMap<>();

        // Initialize standings for all teams
        for (Team team : teams) {
            standingsMap.put(team.getId(), StandingsDTO.builder()
                    .teamId(team.getId())
                    .teamName(team.getName())
                    .played(0)
                    .won(0)
                    .drawn(0)
                    .lost(0)
                    .goalsFor(0)
                    .goalsAgainst(0)
                    .goalDifference(0)
                    .points(0)
                    .build());
        }

        // Calculate stats
        for (Match match : matches) {
            if (!standingsMap.containsKey(match.getHomeTeam().getId()) || !standingsMap.containsKey(match.getAwayTeam().getId())) {
                continue;
            }
            
            StandingsDTO homeStats = standingsMap.get(match.getHomeTeam().getId());
            StandingsDTO awayStats = standingsMap.get(match.getAwayTeam().getId());

            homeStats.setPlayed(homeStats.getPlayed() + 1);
            awayStats.setPlayed(awayStats.getPlayed() + 1);

            homeStats.setGoalsFor(homeStats.getGoalsFor() + match.getHomeGoals());
            homeStats.setGoalsAgainst(homeStats.getGoalsAgainst() + match.getAwayGoals());
            awayStats.setGoalsFor(awayStats.getGoalsFor() + match.getAwayGoals());
            awayStats.setGoalsAgainst(awayStats.getGoalsAgainst() + match.getHomeGoals());

            if (match.getHomeGoals() > match.getAwayGoals()) {
                homeStats.setWon(homeStats.getWon() + 1);
                homeStats.setPoints(homeStats.getPoints() + 3);
                awayStats.setLost(awayStats.getLost() + 1);
            } else if (match.getHomeGoals() < match.getAwayGoals()) {
                awayStats.setWon(awayStats.getWon() + 1);
                awayStats.setPoints(awayStats.getPoints() + 3);
                homeStats.setLost(homeStats.getLost() + 1);
            } else {
                homeStats.setDrawn(homeStats.getDrawn() + 1);
                homeStats.setPoints(homeStats.getPoints() + 1);
                awayStats.setDrawn(awayStats.getDrawn() + 1);
                awayStats.setPoints(awayStats.getPoints() + 1);
            }
        }

        // Calculate Goal Difference and Sort
        List<StandingsDTO> standings = new ArrayList<>(standingsMap.values());
        for (StandingsDTO dto : standings) {
            dto.setGoalDifference(dto.getGoalsFor() - dto.getGoalsAgainst());
        }

        standings.sort(Comparator.comparingInt(StandingsDTO::getPoints).reversed()
                .thenComparingInt(StandingsDTO::getGoalDifference).reversed()
                .thenComparingInt(StandingsDTO::getGoalsFor).reversed());

        // Assign positions
        for (int i = 0; i < standings.size(); i++) {
            standings.get(i).setPosition(i + 1);
        }

        return ResponseEntity.ok(standings);
    }
}
