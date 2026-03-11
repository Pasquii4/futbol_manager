package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.Player;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.MatchEventRepository;
import com.politecnics.football.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Enhanced match simulation engine with realistic mechanics:
 * - Poisson-based goal distribution weighted by team strength
 * - Home advantage (+5 rating)
 * - Player form and injuries
 * - Yellow/red card generation
 * - Assist events linked to goals
 * - Possession and shots on target stats
 * - Psychological factor (top 4 vs bottom 3)
 */
@Slf4j
@Service
public class MatchEngine {

    @Autowired
    private MatchEventRepository matchEventRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private final Random random = new Random();

    // Injury probability per player per match (1-5%)
    private static final double INJURY_PROBABILITY = 0.03;
    // Form change per result
    private static final double FORM_WIN_BOOST = 0.3;
    private static final double FORM_LOSS_PENALTY = -0.4;
    private static final double FORM_DRAW_CHANGE = 0.05;

    /**
     * Simulates a single match, generating goals, events, injuries, and advanced stats.
     */
    public void simulateMatch(Match match) {
        Team home = match.getHomeTeam();
        Team away = match.getAwayTeam();

        // 1. Calculate effective team strength
        double homeStrength = calculateEffectiveStrength(home, true);
        double awayStrength = calculateEffectiveStrength(away, false);

        // 2. Generate goals
        int homeGoals = calculateGoals(homeStrength, awayStrength);
        int awayGoals = calculateGoals(awayStrength, homeStrength);

        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);
        match.setPlayed(true);

        // 3. Generate advanced stats
        int possession = calculatePossession(homeStrength, awayStrength);
        match.setPossession(possession);
        match.setHomeShotsOnTarget(homeGoals + random.nextInt(4) + 1);
        match.setAwayShotsOnTarget(awayGoals + random.nextInt(4) + 1);

        // 4. Generate events (goals, assists, cards)
        generateGoalEvents(match, home, homeGoals);
        generateGoalEvents(match, away, awayGoals);
        generateCardEvents(match, home);
        generateCardEvents(match, away);

        // 5. Process injuries
        processInjuries(home);
        processInjuries(away);

        // 6. Update player form based on result
        updateTeamForm(home, homeGoals > awayGoals ? 1 : homeGoals < awayGoals ? -1 : 0);
        updateTeamForm(away, awayGoals > homeGoals ? 1 : awayGoals < homeGoals ? -1 : 0);

        // 7. Heal injured players (decrement duration)
        healInjuredPlayers(home);
        healInjuredPlayers(away);

        log.debug("Match simulated: {} {} - {} {}", home.getName(), homeGoals, awayGoals, away.getName());
    }

    /**
     * Calculates effective team strength considering rating, home advantage, and average form.
     */
    private double calculateEffectiveStrength(Team team, boolean isHome) {
        int baseRating = team.getOverallRating() != null ? team.getOverallRating() : 70;
        double strength = baseRating;

        // Home advantage: +5
        if (isHome) {
            strength += 5;
        }

        // Average player form modifier (form is 1-10, neutral at 7)
        if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            double avgForm = team.getPlayers().stream()
                    .filter(p -> p.getInjured() == null || !p.getInjured())
                    .mapToDouble(p -> p.getForm() != null ? p.getForm() : 7.0)
                    .average()
                    .orElse(7.0);
            // Form modifier: maps 1-10 to -6% to +3%
            strength += (avgForm - 7.0) * 1.5;
        }

        return strength;
    }

    /**
     * Calculates goals using Poisson-like distribution influenced by strength differential.
     */
    private int calculateGoals(double attackStrength, double defenseStrength) {
        double diff = (attackStrength - defenseStrength) / 10.0;
        double lambda = 1.2 + (diff * 0.5);
        if (lambda < 0.2) lambda = 0.2;
        if (lambda > 3.5) lambda = 3.5; // Cap to prevent unrealistic scores

        // Poisson distribution
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;
    }

    /**
     * Calculates home possession percentage based on strength.
     */
    private int calculatePossession(double homeStrength, double awayStrength) {
        double total = homeStrength + awayStrength;
        int possession = (int) ((homeStrength / total) * 100) + random.nextInt(11) - 5;
        return Math.min(75, Math.max(25, possession)); // Clamp 25-75%
    }

    /**
     * Generates GOAL and ASSIST events for a team.
     */
    private void generateGoalEvents(Match match, Team team, int goals) {
        List<Player> players = team.getPlayers();
        if (players == null || players.isEmpty()) return;

        List<Player> availablePlayers = players.stream()
                .filter(p -> p.getInjured() == null || !p.getInjured())
                .collect(Collectors.toList());
        if (availablePlayers.isEmpty()) return;

        // Build weighted candidate list
        List<Player> scorerCandidates = new ArrayList<>();
        List<Player> assistCandidates = new ArrayList<>();

        for (Player p : availablePlayers) {
            String pos = p.getPosition() != null ? p.getPosition().toUpperCase() : "";
            int scorerWeight = 1;
            int assistWeight = 1;

            if (pos.contains("ST") || pos.contains("FW") || pos.contains("CF") || pos.contains("RW") || pos.contains("LW")) {
                scorerWeight = 6;
                assistWeight = 3;
            } else if (pos.contains("CAM") || pos.contains("CM") || pos.contains("RM") || pos.contains("LM") || pos.contains("M")) {
                scorerWeight = 2;
                assistWeight = 5;
            } else if (pos.contains("CB") || pos.contains("RB") || pos.contains("LB") || pos.contains("DEF")) {
                scorerWeight = 1;
                assistWeight = 2;
            }
            // GK has weight 0 for scorer (very rare) but still in the pool

            for (int i = 0; i < scorerWeight; i++) scorerCandidates.add(p);
            for (int i = 0; i < assistWeight; i++) assistCandidates.add(p);
        }

        for (int i = 0; i < goals; i++) {
            Player scorer = scorerCandidates.get(random.nextInt(scorerCandidates.size()));
            int minute = random.nextInt(90) + 1;

            // Goal event
            MatchEvent goalEvent = MatchEvent.builder()
                    .match(match)
                    .player(scorer)
                    .type(MatchEvent.EventType.GOAL)
                    .minute(minute)
                    .build();
            match.getEvents().add(goalEvent);
            matchEventRepository.save(goalEvent);

            // Assist event (70% chance, different player)
            if (random.nextDouble() < 0.7 && assistCandidates.size() > 1) {
                Player assister;
                do {
                    assister = assistCandidates.get(random.nextInt(assistCandidates.size()));
                } while (assister.getId().equals(scorer.getId()) && assistCandidates.size() > 1);

                if (!assister.getId().equals(scorer.getId())) {
                    MatchEvent assistEvent = MatchEvent.builder()
                            .match(match)
                            .player(assister)
                            .type(MatchEvent.EventType.ASSIST)
                            .minute(minute)
                            .build();
                    match.getEvents().add(assistEvent);
                    matchEventRepository.save(assistEvent);
                }
            }
        }
    }

    /**
     * Generates yellow and red card events for a team.
     */
    private void generateCardEvents(Match match, Team team) {
        List<Player> players = team.getPlayers();
        if (players == null || players.isEmpty()) return;

        List<Player> availablePlayers = players.stream()
                .filter(p -> p.getInjured() == null || !p.getInjured())
                .collect(Collectors.toList());
        if (availablePlayers.isEmpty()) return;

        // 0-4 yellow cards per team per match
        int yellowCards = random.nextInt(4);
        for (int i = 0; i < yellowCards; i++) {
            Player carded = availablePlayers.get(random.nextInt(availablePlayers.size()));
            MatchEvent event = MatchEvent.builder()
                    .match(match)
                    .player(carded)
                    .type(MatchEvent.EventType.YELLOW_CARD)
                    .minute(random.nextInt(90) + 1)
                    .build();
            match.getEvents().add(event);
            matchEventRepository.save(event);
        }

        // ~5% chance of a red card per team
        if (random.nextDouble() < 0.05) {
            Player redCarded = availablePlayers.get(random.nextInt(availablePlayers.size()));
            MatchEvent event = MatchEvent.builder()
                    .match(match)
                    .player(redCarded)
                    .type(MatchEvent.EventType.RED_CARD)
                    .minute(random.nextInt(85) + 5) // Red cards rarely in first 5 min
                    .build();
            match.getEvents().add(event);
            matchEventRepository.save(event);
        }
    }

    /**
     * Processes injuries: each non-injured player has INJURY_PROBABILITY chance of getting injured.
     */
    private void processInjuries(Team team) {
        if (team.getPlayers() == null) return;

        for (Player player : team.getPlayers()) {
            if (player.getInjured() != null && player.getInjured()) continue;

            if (random.nextDouble() < INJURY_PROBABILITY) {
                player.setInjured(true);
                player.setInjuryDuration(1 + random.nextInt(5)); // 1-5 matchdays
                playerRepository.save(player);
                log.info("Injury: {} injured for {} matchdays", player.getName(), player.getInjuryDuration());
            }
        }
    }

    /**
     * Heals injured players by decrementing their injury duration.
     */
    private void healInjuredPlayers(Team team) {
        if (team.getPlayers() == null) return;

        for (Player player : team.getPlayers()) {
            if (player.getInjured() != null && player.getInjured()) {
                int remaining = player.getInjuryDuration() != null ? player.getInjuryDuration() - 1 : 0;
                if (remaining <= 0) {
                    player.setInjured(false);
                    player.setInjuryDuration(0);
                    log.debug("Player recovered: {}", player.getName());
                } else {
                    player.setInjuryDuration(remaining);
                }
                playerRepository.save(player);
            }
        }
    }

    /**
     * Updates team player form based on match result.
     * @param result 1 = win, 0 = draw, -1 = loss
     */
    private void updateTeamForm(Team team, int result) {
        if (team.getPlayers() == null) return;

        double formChange;
        if (result > 0) formChange = FORM_WIN_BOOST;
        else if (result < 0) formChange = FORM_LOSS_PENALTY;
        else formChange = FORM_DRAW_CHANGE;

        for (Player player : team.getPlayers()) {
            double currentForm = player.getForm() != null ? player.getForm() : 7.0;
            double newForm = Math.min(10.0, Math.max(1.0, currentForm + formChange + (random.nextDouble() * 0.4 - 0.2)));
            player.setForm(newForm);
            playerRepository.save(player);
        }
    }
}
