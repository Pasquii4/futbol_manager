package com.politecnics.football.engine;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Player;
import com.politecnics.football.entity.Team;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class MatchSimulationEngine {

    private final Random random = new Random();

    /**
     * Simulates a match and updates the Match entity with the result.
     * Does NOT save to DB.
     *
     * @param match The match to simulate
     */
    public void simulateMatch(Match match) {
        Team home = match.getHomeTeam();
        Team away = match.getAwayTeam();

        // Calculate team strengths (Base rating + Home advantage + Random form)
        // Home advantage: +5 points
        double homeStrength = (home.getOverallRating() * 1.0) + 5.0 + (random.nextGaussian() * 5); 
        double awayStrength = (away.getOverallRating() * 1.0) + (random.nextGaussian() * 5);

        // Calculate goal expectancy based on strength difference
        // Difference of 10 points ~ 1 goal difference expectancy
        double strengthDiff = homeStrength - awayStrength;
        
        // Base goals for an average match (e.g. 2.5 total goals)
        double baseGoals = 2.5;
        
        // Poisson lambda for each team
        // If strengths are equal, lambda is ~1.25 each
        // Adjust lambda based on difference
        double homeLambda = (baseGoals / 2.0) + (strengthDiff / 20.0);
        double awayLambda = (baseGoals / 2.0) - (strengthDiff / 20.0);

        // Clamp lambdas to avoid negative goals
        homeLambda = Math.max(0.2, homeLambda);
        awayLambda = Math.max(0.2, awayLambda);

        int homeGoals = getPoisson(homeLambda);
        int awayGoals = getPoisson(awayLambda);

        match.setHomeGoals(homeGoals);
        match.setAwayGoals(awayGoals);
        match.setPlayed(true);

        // Assign goalscorers (Generic logic for now)
        // This would be handled by StatsUpdateService ideally, but we can stick simple here
    }

    private int getPoisson(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;
    }
}
