package com.politecnics.football.controller;

import com.politecnics.football.service.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulate")
@CrossOrigin(origins = "*")
public class SimulationController {

    @Autowired
    private LeagueService leagueService;

    @PostMapping("/matchday")
    public ResponseEntity<String> simulateMatchday() {
        try {
            String result = leagueService.simulateNextMatchday();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Simulation failed: " + e.getMessage());
        }
    }
    
    // Future: /season
}
