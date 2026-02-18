package com.politecnics.football.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class SimulationService {

    @Autowired
    private com.politecnics.football.repository.MatchRepository matchRepository;
    
    @Autowired
    private com.politecnics.football.engine.MatchSimulationEngine matchSimulationEngine;
    
    @Autowired
    private StatsUpdateService statsUpdateService;

    public void simulateMatchday() {
        // Find next unplayed matchday
        Integer nextMatchday = matchRepository.findByPlayed(false).stream()
                .map(com.politecnics.football.entity.Match::getMatchday)
                .min(Integer::compareTo)
                .orElse(null);

        if (nextMatchday == null) {
            System.out.println("No more matchdays to simulate.");
            return;
        }

        System.out.println("Simulating matchday " + nextMatchday + "...");
        List<com.politecnics.football.entity.Match> matches = matchRepository.findByMatchday(nextMatchday);
        
        for (com.politecnics.football.entity.Match match : matches) {
            if (!match.isPlayed()) {
                matchSimulationEngine.simulateMatch(match);
                matchRepository.save(match);
                statsUpdateService.updateStatsCoordinates(match);
            }
        }
    }

    public void simulateSeason() {
        // Simulate all remaining matches
        // Optimization: Could be done in batch, but loop is fine for 380 matches
        long unplayedCount = matchRepository.findByPlayed(false).size();
        System.out.println("Simulating remaining season (" + unplayedCount + " matches)...");
        
        while (true) {
            Integer nextMatchday = matchRepository.findByPlayed(false).stream()
                    .map(com.politecnics.football.entity.Match::getMatchday)
                    .min(Integer::compareTo)
                    .orElse(null);
                    
            if (nextMatchday == null) break;
            
            simulateMatchday();
        }
    }
}
