package com.politecnics.football.service;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Player;
import com.politecnics.football.repository.MatchRepository;
import com.politecnics.football.repository.PlayerRepository;
import com.politecnics.football.service.simulation.MatchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeagueService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchEngine matchEngine;

    @Transactional
    public String simulateNextMatchday() {
        // 1. Determine current matchday
        Integer currentMatchday = determineCurrentMatchday();
        if (currentMatchday == null) {
            return "Season finished or no matches found.";
        }

        // 2. Get matches for valid matchday
        List<Match> matches = matchRepository.findByMatchday(currentMatchday);
        
        // 3. Simulate each match
        for (Match match : matches) {
            if (!match.isPlayed()) {
                matchEngine.simulateMatch(match);
                matchRepository.save(match);
                
                // 4. Update Player Stats immediately (or could be done in batch)
                updatePlayerStats(match);
            }
        }

        return "Matchday " + currentMatchday + " simulated successfully.";
    }
    
    // Determine the first matchday that has unplayed matches
    private Integer determineCurrentMatchday() {
        Match nextMatch = matchRepository.findFirstByPlayedFalseOrderByMatchdayAsc();
        return nextMatch != null ? nextMatch.getMatchday() : null;
    }

    private void updatePlayerStats(Match match) {
        match.getEvents().forEach(event -> {
            Player player = event.getPlayer();
            if (player != null) {
                switch (event.getType()) {
                    case GOAL:
                        player.setGoalsScored(player.getGoalsScored() + 1);
                        break;
                    case ASSIST:
                        player.setAssists(player.getAssists() + 1);
                        break;
                    case YELLOW_CARD:
                        player.setYellowCards(player.getYellowCards() + 1);
                        break;
                    case RED_CARD:
                        player.setRedCards(player.getRedCards() + 1);
                        break;
                }
                playerRepository.save(player);
            }
        });
        
        // Update general stats like matches played
        updateMatchPlayed(match.getHomeTeam().getPlayers());
        updateMatchPlayed(match.getAwayTeam().getPlayers());
    }

    private void updateMatchPlayed(List<Player> players) {
        // Simple logic: everyone available played. 
        // Improvement: MatchEngine should determine Lineups first.
        // For now, assume all squad players get +1 match (Simplified) -> actually this is bad.
        // Better: Only players involved in events? No. 
        // For Phase 2 MVP: Randomly select 11 players to get +1 match played?
        // Let's leave this simple: If we don't have lineups, maybe just increment matchesPlayed for the whole team? 
        // No, that's unrealistic. 
        // Use a random subset of 15 players per team.
        if (players == null || players.isEmpty()) return;
        
        // Shuffle and pick 14-16
        // For now, let's just say "Top 11 by Overall" played.
        players.stream()
                .sorted((p1, p2) -> p2.getOverall().compareTo(p1.getOverall()))
                .limit(14)
                .forEach(p -> {
                    p.setMatchesPlayed(p.getMatchesPlayed() + 1);
                    playerRepository.save(p);
                });
    }
}
