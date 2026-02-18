package com.politecnics.football.service;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Player;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class StatsUpdateService {

    @Autowired
    private PlayerRepository playerRepository;

    private final Random random = new Random();

    @Transactional
    public void updateStatsCoordinates(Match match) {
        // Update stats for home team
        distributeGoals(match.getHomeTeam(), match.getHomeGoals());
        updateMatchPlayed(match.getHomeTeam());
        
        // Update stats for away team
        distributeGoals(match.getAwayTeam(), match.getAwayGoals());
        updateMatchPlayed(match.getAwayTeam());
    }

    private void updateMatchPlayed(Team team) {
        if (team.getPlayers() == null) return;
        // Assume first 11 players played for simplicity
        // In a real manager, we would select line-ups
        int playersToUpdate = Math.min(11, team.getPlayers().size());
        for (int i = 0; i < playersToUpdate; i++) {
            Player p = team.getPlayers().get(i);
            p.setMatchesPlayed(p.getMatchesPlayed() + 1);
            // Small chance of yellow card
            if (random.nextDouble() < 0.15) {
                p.setYellowCards(p.getYellowCards() + 1);
            }
            playerRepository.save(p);
        }
    }

    private void distributeGoals(Team team, int goals) {
        if (goals == 0 || team.getPlayers() == null || team.getPlayers().isEmpty()) return;

        // Give higher probability to attackers
        List<Player> attackers = team.getPlayers().stream()
                .filter(p -> "ST".equals(p.getPosition()) || "RW".equals(p.getPosition()) || "LW".equals(p.getPosition()))
                .collect(Collectors.toList());
        
        List<Player> midfielders = team.getPlayers().stream()
                .filter(p -> p.getPosition().contains("M") && !attackers.contains(p))
                .collect(Collectors.toList());

        List<Player> candidates = new ArrayList<>();
        // Weight: Attackers 3x entries, Midfielders 1x entry
        if (!attackers.isEmpty()) {
            for (Player p : attackers) {
                candidates.add(p);
                candidates.add(p);
                candidates.add(p);
            }
        }
        if (!midfielders.isEmpty()) {
            candidates.addAll(midfielders);
        }
        // Fallback to all players if no forwards/mids
        if (candidates.isEmpty()) {
            candidates.addAll(team.getPlayers());
        }

        for (int i = 0; i < goals; i++) {
            Player scorer = candidates.get(random.nextInt(candidates.size()));
            scorer.setGoalsScored(scorer.getGoalsScored() + 1);
            playerRepository.save(scorer);
        }
    }
}
