package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.Player;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.MatchEventRepository;
import com.politecnics.football.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

@Service
public class MatchEngine {

    @Autowired
    private MatchEventRepository matchEventRepository;

    private final Random random = new Random();

    public void simulateMatch(Match match) {
        Team home = match.getHomeTeam();
        Team away = match.getAwayTeam();

        // 1. Calculate Team Strength (Base + Home Advantage)
        int homeStrength = (home.getOverallRating() != null ? home.getOverallRating() : 70) + 5; // Home advantage
        int awayStrength = (away.getOverallRating() != null ? away.getOverallRating() : 70);

        // 2. Determine Goals using Poisson-like distribution heavily influenced by strength diff
        int homeGoals = calculateGoals(homeStrength, awayStrength);
        int awayGoals = calculateGoals(awayStrength, homeStrength);

        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);
        match.setPlayed(true);

        // 3. Generate Events (Scorers)
        generateEvents(match, home, homeGoals, true);
        generateEvents(match, away, awayGoals, false);
    }

    private int calculateGoals(int attackStrength, int defenseStrength) {
        double diff = (attackStrength - defenseStrength) / 10.0;
        double lambda = 1.2 + (diff * 0.5); // Base avg goals ~1.2
        if (lambda < 0.2) lambda = 0.2; // Minimum chance

        // Poisson
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;
    }

    private void generateEvents(Match match, Team team, int goals, boolean isHome) {
        List<Player> players = team.getPlayers();
        if (players == null || players.isEmpty()) return;

        for (int i = 0; i < goals; i++) {
            // Select random scorer (weighted by position could be added later)
            Player scorer = players.get(random.nextInt(players.size()));
            
            MatchEvent event = MatchEvent.builder()
                    .match(match)
                    .player(scorer)
                    .type(MatchEvent.EventType.GOAL)
                    .minute(random.nextInt(90) + 1)
                    .build();
            
            match.getEvents().add(event);
            matchEventRepository.save(event);
        }
    }
}
