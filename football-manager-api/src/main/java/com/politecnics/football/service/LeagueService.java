package com.politecnics.football.service;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.repository.MatchRepository;
import com.politecnics.football.repository.JugadorRepository;
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
    private JugadorRepository jugadorRepository;

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
                matchEngine.simulateMatch(match, null);
                matchRepository.save(match);
                
                // 4. Update Jugador Stats immediately (or could be done in batch)
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
            Jugador jugador = event.getJugador();
            if (jugador != null) {
                switch (event.getType()) {
                    case GOAL:
                        jugador.setGoalsScored(jugador.getGoalsScored() + 1);
                        break;
                    case ASSIST:
                        jugador.setAssists(jugador.getAssists() + 1);
                        break;
                    case YELLOW_CARD:
                        jugador.setYellowCards(jugador.getYellowCards() + 1);
                        break;
                    case RED_CARD:
                        jugador.setRedCards(jugador.getRedCards() + 1);
                        break;
                }
                jugadorRepository.save(jugador);
            }
        });
        
        // Update general stats like matches played
        updateMatchPlayed(match.getHomeTeam().getJugadores());
        updateMatchPlayed(match.getAwayTeam().getJugadores());
    }

    private void updateMatchPlayed(List<Jugador> jugadores) {
        // Simple logic: everyone available played. 
        // Improvement: MatchEngine should determine Lineups first.
        // For now, assume all squad jugadores get +1 match (Simplified) -> actually this is bad.
        // Better: Only jugadores involved in events? No. 
        // For Phase 2 MVP: Randomly select 11 jugadores to get +1 match played?
        // Let's leave this simple: If we don't have lineups, maybe just increment matchesPlayed for the whole team? 
        // No, that's unrealistic. 
        // Use a random subset of 15 jugadores per team.
        if (jugadores == null || jugadores.isEmpty()) return;
        
        // Shuffle and pick 14-16
        // For now, let's just say "Top 11 by Overall" played.
        jugadores.stream()
                .sorted((p1, p2) -> p2.getCalidad().compareTo(p1.getCalidad()))
                .limit(14)
                .forEach(p -> {
                    p.setMatchesPlayed(p.getMatchesPlayed() + 1);
                    jugadorRepository.save(p);
                });
    }
}
