package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.repository.MatchEventRepository;
import com.politecnics.football.repository.JugadorRepository;
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
 * - Jugador form and injuries
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
    private JugadorRepository jugadorRepository;

    private final Random random = new Random();

    // Injury probability per jugador per match (1-5%)
    private static final double INJURY_PROBABILITY = 0.03;
    // Form change per result
    private static final double FORM_WIN_BOOST = 0.3;
    private static final double FORM_LOSS_PENALTY = -0.4;
    private static final double FORM_DRAW_CHANGE = 0.05;

    /**
     * Simulates a single match, generating goals, events, injuriesMinute by minute.
     */
    public void simulateMatch(Match match) {
        Equipo home = match.getHomeTeam();
        Equipo away = match.getAwayTeam();
        MatchState state = new MatchState(home, away);

        // 1. Calculate effective team strength
        double homeStrength = calculateEffectiveStrength(home, true);
        double awayStrength = calculateEffectiveStrength(away, false);
        
        // Base chances per minute based on strength
        double homeChancePerMin = (homeStrength / 100.0) * 0.07; 
        double awayChancePerMin = (awayStrength / 100.0) * 0.07;

        if (match.getEvents() == null) {
            match.setEvents(new ArrayList<>());
        }

        // Initialize shots
        match.setHomeShotsOnTarget(0);
        match.setAwayShotsOnTarget(0);

        // 2. The 90-Minute Loop
        for (int minute = 1; minute <= 90; minute++) {
            state.setMinute(minute);

            // Shifting momentum every 15 mins
            if (minute % 15 == 0 || minute == 1) {
                state.setHomeMomentum(homeStrength + random.nextInt(20) - 10);
                state.setAwayMomentum(awayStrength + random.nextInt(20) - 10);
            }

            // Check Home Chance
            if (random.nextDouble() < homeChancePerMin * (state.getHomeMomentum() / 50.0)) {
                if (random.nextDouble() < 0.3) { // 30% conversion for a chance
                    state.setHomeGoals(state.getHomeGoals() + 1);
                    generateSingleGoalEvent(match, home, minute);
                } else {
                    match.setHomeShotsOnTarget(match.getHomeShotsOnTarget() + 1);
                }
            }

            // Check Away Chance
            if (random.nextDouble() < awayChancePerMin * (state.getAwayMomentum() / 50.0)) {
                if (random.nextDouble() < 0.3) {
                    state.setAwayGoals(state.getAwayGoals() + 1);
                    generateSingleGoalEvent(match, away, minute);
                } else {
                    match.setAwayShotsOnTarget(match.getAwayShotsOnTarget() + 1);
                }
            }

            // Cards
            if (random.nextDouble() < 0.015) generateSingleCardEvent(match, home, minute, false);
            if (random.nextDouble() < 0.015) generateSingleCardEvent(match, away, minute, false);
            
            if (random.nextDouble() < 0.001) generateSingleCardEvent(match, home, minute, true);
            if (random.nextDouble() < 0.001) generateSingleCardEvent(match, away, minute, true);
        }

        // 3. Finalize Match State
        match.setHomeGoals(state.getHomeGoals());
        match.setAwayGoals(state.getAwayGoals());
        match.setPlayed(true);

        int possession = calculatePossession(homeStrength, awayStrength);
        match.setPossession(possession);
        
        // Ensure shots on target are at least equal to goals
        if (match.getHomeShotsOnTarget() < match.getHomeGoals()) match.setHomeShotsOnTarget(match.getHomeGoals() + random.nextInt(3));
        if (match.getAwayShotsOnTarget() < match.getAwayGoals()) match.setAwayShotsOnTarget(match.getAwayGoals() + random.nextInt(3));

        // 4. Injuries & Form
        processInjuries(home);
        processInjuries(away);

        updateTeamForm(home, match.getHomeGoals() > match.getAwayGoals() ? 1 : match.getHomeGoals() < match.getAwayGoals() ? -1 : 0);
        updateTeamForm(away, match.getAwayGoals() > match.getHomeGoals() ? 1 : match.getAwayGoals() < match.getHomeGoals() ? -1 : 0);

        healInjuredPlayers(home);
        healInjuredPlayers(away);

        log.debug("Match simulated: {} {} - {} {}", home.getNombre(), match.getHomeGoals(), match.getAwayGoals(), away.getNombre());
    }

    private double calculateEffectiveStrength(Equipo team, boolean isHome) {
        int baseRating = team.getCalidadRating() != null ? team.getCalidadRating() : 70;
        double strength = baseRating;
        if (isHome) strength += 5;

        if (team.getJugadores() != null && !team.getJugadores().isEmpty()) {
            double avgForm = team.getJugadores().stream()
                    .filter(p -> p.getInjured() == null || !p.getInjured())
                    .mapToDouble(p -> p.getForma() != null ? p.getForma() : 7.0)
                    .average()
                    .orElse(7.0);
            strength += (avgForm - 7.0) * 1.5;
        }
        return strength;
    }

    private int calculatePossession(double homeStrength, double awayStrength) {
        double total = homeStrength + awayStrength;
        int possession = (int) ((homeStrength / total) * 100) + random.nextInt(11) - 5;
        return Math.min(75, Math.max(25, possession));
    }

    private void generateSingleGoalEvent(Match match, Equipo team, int minute) {
        List<Jugador> availablePlayers = getAvailablePlayers(team);
        if (availablePlayers.isEmpty()) return;

        List<Jugador> scorerCandidates = new ArrayList<>();
        List<Jugador> assistCandidates = new ArrayList<>();

        for (Jugador p : availablePlayers) {
            String pos = (p.getPosicion() != null ? p.getPosicion().name() : "").toUpperCase();
            int scorerWeight = 1, assistWeight = 1;

            if (pos.contains("ST") || pos.contains("FW") || pos.contains("CF") || pos.contains("RW") || pos.contains("LW")) {
                scorerWeight = 6; assistWeight = 3;
            } else if (pos.contains("CAM") || pos.contains("CM") || pos.contains("RM") || pos.contains("LM") || pos.contains("M")) {
                scorerWeight = 2; assistWeight = 5;
            } else if (pos.contains("CB") || pos.contains("RB") || pos.contains("LB") || pos.contains("DEF")) {
                scorerWeight = 1; assistWeight = 2;
            }

            for (int i = 0; i < scorerWeight; i++) scorerCandidates.add(p);
            for (int i = 0; i < assistWeight; i++) assistCandidates.add(p);
        }

        if(scorerCandidates.isEmpty()) return;
        Jugador scorer = scorerCandidates.get(random.nextInt(scorerCandidates.size()));

        MatchEvent goalEvent = MatchEvent.builder()
                .match(match)
                .jugador(scorer)
                .type(MatchEvent.EventType.GOAL)
                .minute(minute)
                .build();
        match.getEvents().add(goalEvent);
        matchEventRepository.save(goalEvent);

        if (random.nextDouble() < 0.7 && assistCandidates.size() > 1) {
            Jugador assister;
            do {
                assister = assistCandidates.get(random.nextInt(assistCandidates.size()));
            } while (assister.getId().equals(scorer.getId()) && assistCandidates.size() > 1);

            if (!assister.getId().equals(scorer.getId())) {
                MatchEvent assistEvent = MatchEvent.builder()
                        .match(match)
                        .jugador(assister)
                        .type(MatchEvent.EventType.ASSIST)
                        .minute(minute)
                        .build();
                match.getEvents().add(assistEvent);
                matchEventRepository.save(assistEvent);
            }
        }
    }

    private void generateSingleCardEvent(Match match, Equipo team, int minute, boolean isRed) {
        List<Jugador> availablePlayers = getAvailablePlayers(team);
        if (availablePlayers.isEmpty()) return;

        Jugador carded = availablePlayers.get(random.nextInt(availablePlayers.size()));
        MatchEvent event = MatchEvent.builder()
                .match(match)
                .jugador(carded)
                .type(isRed ? MatchEvent.EventType.RED_CARD : MatchEvent.EventType.YELLOW_CARD)
                .minute(minute)
                .build();
        match.getEvents().add(event);
        matchEventRepository.save(event);
    }
    
    private List<Jugador> getAvailablePlayers(Equipo team) {
        if(team.getJugadores() == null) return new ArrayList<>();
        return team.getJugadores().stream()
                .filter(p -> p.getInjured() == null || !p.getInjured())
                .collect(Collectors.toList());
    }

    private void processInjuries(Equipo team) {
        if (team.getJugadores() == null) return;
        for (Jugador jugador : team.getJugadores()) {
            if (jugador.getInjured() != null && jugador.getInjured()) continue;
            if (random.nextDouble() < INJURY_PROBABILITY) {
                jugador.setInjured(true);
                jugador.setInjuryDuration(1 + random.nextInt(5));
                jugadorRepository.save(jugador);
            }
        }
    }

    private void healInjuredPlayers(Equipo team) {
        if (team.getJugadores() == null) return;
        for (Jugador jugador : team.getJugadores()) {
            if (jugador.getInjured() != null && jugador.getInjured()) {
                int remaining = jugador.getInjuryDuration() != null ? jugador.getInjuryDuration() - 1 : 0;
                if (remaining <= 0) {
                    jugador.setInjured(false);
                    jugador.setInjuryDuration(0);
                } else {
                    jugador.setInjuryDuration(remaining);
                }
                jugadorRepository.save(jugador);
            }
        }
    }

    private void updateTeamForm(Equipo team, int result) {
        if (team.getJugadores() == null) return;
        double formChange = (result > 0) ? FORM_WIN_BOOST : (result < 0) ? FORM_LOSS_PENALTY : FORM_DRAW_CHANGE;
        for (Jugador jugador : team.getJugadores()) {
            double currentForm = jugador.getForma() != null ? jugador.getForma() : 7.0;
            double newForm = Math.min(10.0, Math.max(1.0, currentForm + formChange + (random.nextDouble() * 0.4 - 0.2)));
            jugador.setForma(newForm);
            jugadorRepository.save(jugador);
        }
    }
}
