package com.politecnics.football.service;

import com.politecnics.football.entity.Match;
import com.politecnics.football.repository.MatchRepository;
import com.politecnics.football.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimulationServiceTest {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private DataLoadService dataLoadService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void testSimulationCycle() {
        // 1. Data Loading triggers automatically via @PostConstruct, but in test env let's verify
        assertTrue(teamRepository.count() > 0, "Teams should be loaded");
        
        // 2. Fixtures should be generated
        List<Match> allMatches = matchRepository.findAll();
        assertTrue(allMatches.size() > 0, "Matches should be generated");
        
        long unplayedBefore = matchRepository.findByPlayed(false).size();
        System.out.println("Unplayed matches before: " + unplayedBefore);

        // 3. Simulate one matchday
        simulationService.simulateMatchday();

        long unplayedAfter = matchRepository.findByPlayed(false).size();
        System.out.println("Unplayed matches after: " + unplayedAfter);

        assertTrue(unplayedAfter < unplayedBefore, "Some matches should have been played");

        // 4. Check results
        List<Match> playedMatches = matchRepository.findByPlayed(true);
        assertFalse(playedMatches.isEmpty());
        Match m = playedMatches.get(0);
        assertNotNull(m.getHomeGoals());
        assertNotNull(m.getAwayGoals());
        System.out.println("Match result: " + m.getHomeTeam().getName() + " " + m.getHomeGoals() + 
                " - " + m.getAwayGoals() + " " + m.getAwayTeam().getName());
    }
}
