package com.politecnics.football.controller;

import com.politecnics.football.dto.LeagueStatusDTO;
import com.politecnics.football.entity.Match;
import com.politecnics.football.repository.MatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/league")
@CrossOrigin(origins = "*")
public class LeagueController {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private com.politecnics.football.repository.LeagueRepository leagueRepository;

    @Autowired
    private com.politecnics.football.service.DataLoadService dataLoadService;

    @GetMapping("/status")
    public ResponseEntity<LeagueStatusDTO> getLeagueStatus() {
        List<Match> matches = matchRepository.findAll();
        log.debug("League status check: {} total matches found", matches.size());
        
        if (matches.isEmpty()) {
            return ResponseEntity.ok(LeagueStatusDTO.builder()
                    .currentMatchday(0)
                    .totalMatchdays(0)
                    .seasonYear(2025) // Default for empty
                    .isStarted(false)
                    .isFinished(false)
                    .build());
        }

        int totalMatchdays = matches.stream()
                .map(Match::getMatchday)
                .max(Integer::compareTo)
                .orElse(38);

        // Find first matchday that is NOT fully played
        int currentMatchday = matches.stream()
                .filter(m -> !m.isPlayed())
                .map(Match::getMatchday)
                .min(Integer::compareTo)
                .orElse(totalMatchdays + 1); // All played

        // If current > total, it's finished
        boolean isFinished = currentMatchday > totalMatchdays;
        if (isFinished) currentMatchday = totalMatchdays; // cap at last for display or keep as is? 
        // Logic: if all played, we are at "End of Season".

        // Fetch basic league info dynamically
        com.politecnics.football.entity.League league = leagueRepository.findAll().stream().findFirst().orElse(null);
        Long managedTeamId = league != null ? league.getManagedTeamId() : null;
        int seasonYear = league != null && league.getSeasonYear() != null ? league.getSeasonYear() : 2025;

        return ResponseEntity.ok(LeagueStatusDTO.builder()
                .currentMatchday(isFinished ? totalMatchdays : currentMatchday)
                .totalMatchdays(totalMatchdays)
                .seasonYear(seasonYear)
                .isStarted(true)
                .isFinished(isFinished)
                .managedTeamId(managedTeamId)
                .build());
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetLeague() {
        try {
            dataLoadService.resetLeagueData();
            return ResponseEntity.ok("League reset and re-seeded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to reset league: " + e.getMessage());
        }
    }

    @PostMapping("/select-team/{teamId}")
    @Transactional
    public ResponseEntity<String> selectTeam(@PathVariable Long teamId) {
        // Implement logic to set managedTeamId on the active league
        com.politecnics.football.entity.League league = leagueRepository.findAll().stream().findFirst().orElse(null);
        
        if (league == null) {
            // This case should rarely happen if data is initialized
            league = com.politecnics.football.entity.League.builder()
                .name("LaLiga EA Sports")
                .country("Spain")
                .seasonYear(2025) 
                .build();
            league = leagueRepository.save(league);
        }
        
        league.setManagedTeamId(teamId);
        leagueRepository.save(league);
        return ResponseEntity.ok("Equipo selected successfully");
    }
}
