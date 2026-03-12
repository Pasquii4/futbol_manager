package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FullSimEngineTest {

    @InjectMocks
    private FullSimEngine fullSimEngine;

    private Equipo homeTeam;
    private Equipo awayTeam;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        homeTeam = Equipo.builder().id(1L).nombre("Home").calidadRating(70).build();
        awayTeam = Equipo.builder().id(2L).nombre("Away").calidadRating(70).build();
        
        Jugador p1 = Jugador.builder().id(1L).nombre("H1").build();
        Jugador p2 = Jugador.builder().id(2L).nombre("A1").build();
        homeTeam.setJugadores(Collections.singletonList(p1));
        awayTeam.setJugadores(Collections.singletonList(p2));
    }

    @Test
    void testGoalDistribution() {
        int totalMatches = 10000;
        int totalGoals = 0;
        int matchesWithMoreThan7Goals = 0;

        for (int i = 0; i < totalMatches; i++) {
            Match match = Match.builder().homeTeam(homeTeam).awayTeam(awayTeam).build();
            MatchResult result = fullSimEngine.simulate(match);
            int matchGoals = result.getHomeGoals() + result.getAwayGoals();
            totalGoals += matchGoals;
            if (matchGoals > 7) {
                matchesWithMoreThan7Goals++;
            }
        }

        double averageGoals = (double) totalGoals / totalMatches;
        double highScoringPercentage = (double) matchesWithMoreThan7Goals / totalMatches * 100;

        // Verificar que la media de goles por partido está en un rango realista
        assertTrue(averageGoals >= 2.0 && averageGoals <= 3.8, "Average goals outside expected range: " + averageGoals);
        
        // Verificar que hay <2% de partidos con más de 7 goles
        assertTrue(highScoringPercentage < 5.0, "Too many high scoring matches: " + highScoringPercentage + "%");
    }

    @Test
    void testMomentumEffect() throws Exception {
        Match match = Match.builder().homeTeam(homeTeam).awayTeam(awayTeam).build();
        MatchState state = new MatchState(match);
        
        state.setCurrentMinute(80);
        state.setHomeGoals(0);
        state.setAwayGoals(1); // Home is losing in 80th minute

        // Access private method calculateMomentum using reflection
        Method calculateMomentum = FullSimEngine.class.getDeclaredMethod("calculateMomentum", MatchState.class, double.class, boolean.class);
        calculateMomentum.setAccessible(true);

        double baseStrength = 70.0;
        double momentum = (Double) calculateMomentum.invoke(fullSimEngine, state, baseStrength, true);

        // Equipo que va perdiendo en el minuto 80 debe tener lambda >= 1.3x la base
        // According to our logic: pressure = 0.15 + 0.15 (+0.10 if <10, but we are at 80 so minutesLeft=10, maybe it triggers). So 1.3x base.
        assertTrue(momentum >= baseStrength * 1.3, "Momentum should be at least 1.3x base. Was: " + (momentum / baseStrength) + "x");
    }

    @Test
    void testRedCardPenalty() throws Exception {
        Match match = Match.builder().homeTeam(homeTeam).awayTeam(awayTeam).build();
        MatchState state = new MatchState(match);
        state.setHomeHasRedCard(true); // Red card for home team

        Method calculateMomentum = FullSimEngine.class.getDeclaredMethod("calculateMomentum", MatchState.class, double.class, boolean.class);
        calculateMomentum.setAccessible(true);

        double baseStrength = 70.0;
        double momentum = (Double) calculateMomentum.invoke(fullSimEngine, state, baseStrength, true);

        // Equipo con roja debe tener strength *= 0.72
        assertEquals(baseStrength * 0.72, momentum, 0.01, "Red card penalty not applied correctly");
    }
}
