package com.politecnics.football.controller;

import com.politecnics.football.service.LeagueService;
import com.politecnics.football.service.SimulationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for match simulation operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/simulate")
@CrossOrigin(origins = "*")
public class SimulationController {

    @Autowired
    private LeagueService leagueService;

    @Autowired
    private SimulationService simulationService;

    /**
     * POST /api/simulate/matchday - Simulates the next unplayed matchday.
     */
    @PostMapping("/matchday")
    public ResponseEntity<String> simulateMatchday() {
        log.info("Simulating next matchday...");
        String result = leagueService.simulateNextMatchday();
        log.info("Simulation result: {}", result);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/simulate/advance-matchday - Alias for matchday advance, per Phase 1 specs
     */
    @PostMapping("/advance-matchday")
    public ResponseEntity<String> advanceMatchday() {
        return simulateMatchday();
    }

    /**
     * POST /api/simulate/season - Simulates all remaining matchdays.
     */
    @PostMapping("/season")
    public ResponseEntity<String> simulateSeason() {
        log.info("Simulating remaining season...");
        simulationService.simulateSeason();
        return ResponseEntity.ok("Season simulation complete.");
    }
}
