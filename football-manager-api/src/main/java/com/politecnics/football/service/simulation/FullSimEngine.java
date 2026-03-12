package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.MatchStats;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Component
public class FullSimEngine implements SimEngine {

    private static final double BASE_LAMBDA_PER_MINUTE = 1.35 / 90.0;
    private final Random random = new Random();

    @Override
    public MatchResult simulate(Match match) {
        MatchState state = new MatchState(match);
        
        double homeStrength = calculateStrength(match.getHomeTeam(), true);
        double awayStrength = calculateStrength(match.getAwayTeam(), false);
        
        // Bucle principal 1-90
        for (int minute = 1; minute <= 90; minute++) {
            state.setCurrentMinute(minute);
            
            // Momentum dinámico según marcador y tiempo restante
            double homeMomentum = calculateMomentum(state, homeStrength, true);
            double awayMomentum = calculateMomentum(state, awayStrength, false);
            
            // ¿Hay oportunidad este minuto?
            tryOpportunity(state, match.getHomeTeam(), homeMomentum, minute, true);
            tryOpportunity(state, match.getAwayTeam(), awayMomentum, minute, false);
            
            // Sustituciones automáticas IA (minuto 55+)
            if (minute >= 55) {
                processSubstitutions(state, match.getHomeTeam(), true);
                processSubstitutions(state, match.getAwayTeam(), false);
            }
            
            // Eventos especiales (tarjetas no ligadas a gol, lesiones)
            processRandomEvents(state, match.getHomeTeam(), minute, true);
            processRandomEvents(state, match.getAwayTeam(), minute, false);
        }
        
        // Tiempo añadido (3-6 min)
        int addedTime = 3 + random.nextInt(4);
        for (int minute = 91; minute <= 90 + addedTime; minute++) {
            state.setCurrentMinute(minute);
            // En tiempo añadido el equipo perdedor presiona más (+30%)
            double homeMomentum = calculateMomentum(state, homeStrength, true) * 
                                  (state.getHomeGoals() < state.getAwayGoals() ? 1.3 : 1.0);
            double awayMomentum = calculateMomentum(state, awayStrength, false) *
                                  (state.getAwayGoals() < state.getHomeGoals() ? 1.3 : 1.0);
            tryOpportunity(state, match.getHomeTeam(), homeMomentum, minute, true);
            tryOpportunity(state, match.getAwayTeam(), awayMomentum, minute, false);
        }
        
        // Ordenar eventos por minuto
        state.getEvents().sort(Comparator.comparingInt(MatchEvent::getMinute));
        
        return buildResult(state);
    }
    
    private double calculateStrength(Equipo team, boolean isHome) {
        double overall = team.getCalidadRating() != null ? team.getCalidadRating() : 70.0;
        return isHome ? overall * 1.05 : overall; // Ligera ventaja local
    }
    
    private MatchResult buildResult(MatchState state) {
        MatchStats stats = MatchStats.builder()
            .homeXG(state.getHomeXG())
            .awayXG(state.getAwayXG())
            .homeShots(state.getHomeTotalShots())
            .awayShots(state.getAwayTotalShots())
            .homeShotsOnTarget(state.getHomeShotsOnTarget())
            .awayShotsOnTarget(state.getAwayShotsOnTarget())
            .homePossession(50 + (int)(state.getHomeXG() * 10 - state.getAwayXG() * 10)) // Arbitrary mock possession for now
            .build();
            
        // Limit possession bounds
        stats.setHomePossession(Math.min(80, Math.max(20, stats.getHomePossession())));
        
        return MatchResult.builder()
            .homeGoals(state.getHomeGoals())
            .awayGoals(state.getAwayGoals())
            .events(state.getEvents())
            .stats(stats)
            .build();
    }
    
    private double calculateMomentum(MatchState state, double baseStrength, boolean isHome) {
        double momentum = baseStrength;
        int scoreDiff = state.getScoreDiff(isHome);
        int minutesLeft = Math.max(0, 90 - state.getCurrentMinute());
        
        // Equipo perdiendo presiona más
        if (scoreDiff < 0) {
            double pressure = 0.15 + (minutesLeft < 20 ? 0.15 : 0.0) + 
                              (minutesLeft < 10 ? 0.10 : 0.0);
            momentum *= (1.0 + pressure);
        }
        // Equipo ganando puede ser más conservador
        if (scoreDiff > 1 && minutesLeft < 20) momentum *= 0.85;
        
        // Penalización por tarjeta roja
        boolean hasRed = isHome ? state.isHomeHasRedCard() : state.isAwayHasRedCard();
        if (hasRed) momentum *= 0.72;
        
        return momentum;
    }
    
    private void tryOpportunity(MatchState state, Equipo team, double strength, 
                                 int minute, boolean isHome) {
        // Lambda calibrated so that two teams with strength~70 produce ~2.7 total goals
        // Each team ~1.35 goals per match
        double lambda = 0.065 * (strength / 70.0);
        if (random.nextDouble() > lambda) return; // No hay oportunidad este minuto
        
        // Determinar tipo de oportunidad
        OpportunityType type = selectOpportunityType();
        double xG = getXGForType(type);
        
        // Registrar shot
        if (isHome) { state.setHomeTotalShots(state.getHomeTotalShots() + 1); }
        else { state.setAwayTotalShots(state.getAwayTotalShots() + 1); }
        
        // Acumular xG
        if (isHome) state.setHomeXG(state.getHomeXG() + xG);
        else state.setAwayXG(state.getAwayXG() + xG);
        
        // ¿Es gol? xG IS the conversion probability
        if (random.nextDouble() < xG) {
            // A goal is always on target
            if (isHome) state.setHomeShotsOnTarget(state.getHomeShotsOnTarget() + 1);
            else state.setAwayShotsOnTarget(state.getAwayShotsOnTarget() + 1);
            scoreGoal(state, team, minute, isHome, type);
        } else {
            // Not a goal, but might still be on target (saved by keeper)
            boolean isOnTarget = random.nextDouble() < 0.35;
            if (isOnTarget) {
                if (isHome) state.setHomeShotsOnTarget(state.getHomeShotsOnTarget() + 1);
                else state.setAwayShotsOnTarget(state.getAwayShotsOnTarget() + 1);
            }
        }
    }
    
    private void scoreGoal(MatchState state, Equipo team, int minute, 
                            boolean isHome, OpportunityType type) {
        if (isHome) state.setHomeGoals(state.getHomeGoals() + 1);
        else state.setAwayGoals(state.getAwayGoals() + 1);
        
        // Seleccionar goleador ponderado por posición
        Jugador scorer = selectWeightedScorer(team.getJugadores());
        if (scorer == null) return;
        
        Jugador assister = random.nextDouble() < 0.70 ? 
                          selectWeightedAssister(team.getJugadores(), scorer) : null;
        
        // Crear evento GOL
        MatchEvent goalEvent = MatchEvent.builder()
            .match(state.getMatch()).jugador(scorer)
            .type(MatchEvent.EventType.GOAL).minute(minute)
            .build();
        state.getEvents().add(goalEvent);
        
        // Crear evento ASSIST
        if (assister != null) {
            MatchEvent assistEvent = MatchEvent.builder()
                .match(state.getMatch()).jugador(assister)
                .type(MatchEvent.EventType.ASSIST).minute(minute)
                .build();
            state.getEvents().add(assistEvent);
        }
    }
    
    private Jugador selectWeightedScorer(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) return null;
        // Simple random pick for now
        return jugadores.get(random.nextInt(jugadores.size()));
    }
    
    private Jugador selectWeightedAssister(List<Jugador> jugadores, Jugador scorer) {
        if (jugadores == null || jugadores.size() <= 1) return null;
        Jugador assister;
        do {
            assister = jugadores.get(random.nextInt(jugadores.size()));
        } while (assister.getId().equals(scorer.getId()));
        return assister;
    }
    
    private void processSubstitutions(MatchState state, Equipo team, boolean isHome) {
        int subsUsed = isHome ? state.getHomeSubstitutionsUsed() : 
                                state.getAwaySubstitutionsUsed();
        if (subsUsed >= 5) return; // 5 subs allowed nowadays
        // Solo 1 sustitución por llamada para no saturar el minuto
        if (random.nextDouble() > 0.05) return;
        
        if (team.getJugadores() == null || team.getJugadores().isEmpty()) return;
        
        Jugador out = team.getJugadores().get(random.nextInt(team.getJugadores().size()));
        
        // Incrementar contador de subs del estado
        if (isHome) state.setHomeSubstitutionsUsed(subsUsed + 1);
        else state.setAwaySubstitutionsUsed(subsUsed + 1);
        
        // Crear MatchEvent SUBSTITUTION
        MatchEvent subEvent = MatchEvent.builder()
            .match(state.getMatch())
            .jugador(out) // The one going out (could add 'in' player in the future)
            .type(MatchEvent.EventType.SUBSTITUTION)
            .minute(state.getCurrentMinute())
            .build();
        state.getEvents().add(subEvent);
    }
    
    private void processRandomEvents(MatchState state, Equipo team, int minute, boolean isHome) {
        if (team.getJugadores() == null) return;
        for (Jugador p : team.getJugadores()) {
            // Tarjeta amarilla: 0.05% por jugador por minuto
            if (random.nextDouble() < 0.0005) {
                MatchEvent yellowCard = MatchEvent.builder()
                    .match(state.getMatch()).jugador(p)
                    .type(MatchEvent.EventType.YELLOW_CARD).minute(minute)
                    .build();
                state.getEvents().add(yellowCard);
            }
            // Tarjeta roja directa: 0.005% por jugador por minuto
            if (random.nextDouble() < 0.00005) {
                if (isHome) state.setHomeHasRedCard(true);
                else state.setAwayHasRedCard(true);
                MatchEvent redCard = MatchEvent.builder()
                    .match(state.getMatch()).jugador(p)
                    .type(MatchEvent.EventType.RED_CARD).minute(minute)
                    .build();
                state.getEvents().add(redCard);
            }
            // Lesión: 0.03% por jugador por minuto
            if (random.nextDouble() < 0.0003) {
                p.setInjured(true);
                p.setInjuryDuration(1 + random.nextInt(5));
                MatchEvent injury = MatchEvent.builder()
                    .match(state.getMatch()).jugador(p)
                    .type(MatchEvent.EventType.INJURY).minute(minute)
                    .build();
                state.getEvents().add(injury);
            }
        }
    }
    
    // Tipos de oportunidad con sus probabilidades base
    private OpportunityType selectOpportunityType() {
        double r = random.nextDouble();
        if (r < 0.05) return OpportunityType.PENALTY;
        if (r < 0.15) return OpportunityType.CLEAR_CHANCE;
        if (r < 0.35) return OpportunityType.HEADER;
        if (r < 0.60) return OpportunityType.CLOSE_RANGE;
        return OpportunityType.LONG_SHOT;
    }
    
    private double getXGForType(OpportunityType type) {
        switch (type) {
            case PENALTY: return 0.76;
            case CLEAR_CHANCE: return 0.35 + random.nextDouble() * 0.25;
            case HEADER: return 0.12 + random.nextDouble() * 0.13;
            case CLOSE_RANGE: return 0.18 + random.nextDouble() * 0.12;
            case LONG_SHOT: return 0.04 + random.nextDouble() * 0.06;
            default: return 0.1;
        }
    }
}
