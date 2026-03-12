package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.MatchStats;
import com.politecnics.football.entity.Equipo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class FastSimEngine implements SimEngine {
    
    private final Random random = new Random();
    
    @Override
    public MatchResult simulate(Match match) {
        double homeStr = calculateStrength(match.getHomeTeam(), true);
        double awayStr = calculateStrength(match.getAwayTeam(), false);
        
        // Lambda directa para todo el partido
        double homeLambda = 0.3 + (homeStr / awayStr) * 0.9;
        double awayLambda = 0.3 + (awayStr / homeStr) * 0.9;
        homeLambda = Math.min(3.5, Math.max(0.2, homeLambda));
        awayLambda = Math.min(3.5, Math.max(0.2, awayLambda));
        
        int homeGoals = poissonRandom(homeLambda);
        int awayGoals = poissonRandom(awayLambda);
        
        // Generar eventos mínimos (solo goles + tarjetas, sin minuto preciso)
        List<MatchEvent> events = generateMinimalEvents(match, homeGoals, awayGoals);
        
        // Stats sintéticas
        MatchStats stats = MatchStats.builder()
            .homeXG(homeLambda * 0.8).awayXG(awayLambda * 0.8)
            .homePossession(calculatePossession(homeStr, awayStr))
            .homeShots(homeGoals + random.nextInt(5) + 3)
            .awayShots(awayGoals + random.nextInt(5) + 3)
            .homeShotsOnTarget(homeGoals + random.nextInt(3))
            .awayShotsOnTarget(awayGoals + random.nextInt(3))
            .build();
        
        return MatchResult.builder()
            .homeGoals(homeGoals).awayGoals(awayGoals)
            .events(events).stats(stats)
            .build();
    }
    
    private int poissonRandom(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0; int k = 0;
        do { k++; p *= random.nextDouble(); } while (p > L);
        return k - 1;
    }
    
    private double calculateStrength(Equipo equipo, boolean isHome) {
        double overall = equipo.getCalidadRating() != null ? equipo.getCalidadRating() : 70.0;
        return isHome ? overall * 1.05 : overall; // Ligera ventaja local
    }
    
    private int calculatePossession(double homeStr, double awayStr) {
        double homeProb = homeStr / (homeStr + awayStr);
        double possession = homeProb * 100.0;
        // Añadir algo de varianza
        possession += (random.nextDouble() * 10 - 5);
        return (int) Math.min(80, Math.max(20, possession));
    }
    
    private List<MatchEvent> generateMinimalEvents(Match match, int homeGoals, int awayGoals) {
        List<MatchEvent> events = new ArrayList<>();
        // Asignar goles a jugadores aleatorios del equipo
        generateGoals(events, match, match.getHomeTeam(), homeGoals);
        generateGoals(events, match, match.getAwayTeam(), awayGoals);
        return events;
    }
    
    private void generateGoals(List<MatchEvent> events, Match match, Equipo team, int goals) {
        if (team.getJugadores() == null || team.getJugadores().isEmpty()) return;
        List<Jugador> jugadores = team.getJugadores();
        
        for (int i = 0; i < goals; i++) {
            // Pick random player
            Jugador scorer = jugadores.get(random.nextInt(jugadores.size()));
            int min = random.nextInt(90) + 1;
            
            MatchEvent goalEvent = MatchEvent.builder()
                .match(match)
                .jugador(scorer)
                .type(MatchEvent.EventType.GOAL)
                .minute(min)
                .build();
            events.add(goalEvent);
        }
    }
}
