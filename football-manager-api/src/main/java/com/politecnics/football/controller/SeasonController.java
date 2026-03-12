package com.politecnics.football.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.politecnics.football.dto.StandingsDTO;
import com.politecnics.football.entity.*;
import com.politecnics.football.repository.*;
import com.politecnics.football.service.DataLoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for historical season management.
 * Allows viewing past seasons and starting a new one.
 */
@Slf4j
@RestController
@RequestMapping("/api/seasons")
@CrossOrigin(origins = "*")
public class SeasonController {

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private LigaRepository ligaRepository;

    @Autowired
    private DataLoadService dataLoadService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * GET /api/seasons - Get all historical seasons.
     */
    @GetMapping
    public ResponseEntity<List<Season>> getAllSeasons() {
        return ResponseEntity.ok(seasonRepository.findAllByOrderBySeasonYearDesc());
    }

    /**
     * GET /api/seasons/{id} - Get a specific season.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Season> getSeason(@PathVariable Long id) {
        return seasonRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/seasons/new - Save current season and start a new one.
     * Takes a snapshot of the current standings, determines champion and top scorer,
     * then resets the league for a new season.
     */
    @PostMapping("/new")
    @Transactional
    public ResponseEntity<String> startNewSeason() {
        log.info("Starting new season...");

        // 1. Calculate final standings
        List<StandingsDTO> standings = calculateStandings();
        if (standings.isEmpty()) {
            return ResponseEntity.badRequest().body("No season data to save.");
        }

        // 2. Find champion
        String champion = standings.get(0).getTeamName();

        // 3. Find top scorer
        List<Jugador> jugadores = jugadorRepository.findTop20ByOrderByGoalsScoredDesc();
        String topScorer = "N/A";
        int topGoals = 0;
        if (!jugadores.isEmpty()) {
            topScorer = jugadores.get(0).getNombre();
            topGoals = jugadores.get(0).getGoalsScored() != null ? jugadores.get(0).getGoalsScored() : 0;
        }

        // 4. Get season year
        Liga league = ligaRepository.findAll().stream().findFirst().orElse(null);
        int seasonYear = league != null && league.getSeasonYear() != null ? league.getSeasonYear() : 2025;

        // 5. Serialize standings snapshot
        String snapshot;
        try {
            snapshot = objectMapper.writeValueAsString(standings);
        } catch (Exception e) {
            snapshot = "[]";
        }

        // 6. Save season
        Season season = Season.builder()
                .seasonYear(seasonYear)
                .champion(champion)
                .topScorer(topScorer)
                .topScorerGoals(topGoals)
                .standingsSnapshot(snapshot)
                .build();
        seasonRepository.save(season);
        log.info("Season {} saved. Champion: {}, Top Scorer: {} ({})", seasonYear, champion, topScorer, topGoals);

        // 7. Reset league for next season
        dataLoadService.resetLeagueData();

        // 8. Update season year
        league = ligaRepository.findAll().stream().findFirst().orElse(null);
        if (league != null) {
            league.setSeasonYear(seasonYear + 1);
            ligaRepository.save(league);
        }

        return ResponseEntity.ok("New season " + (seasonYear + 1) + " started. Previous champion: " + champion);
    }

    private List<StandingsDTO> calculateStandings() {
        List<Equipo> teams = equipoRepository.findAll();
        List<Match> matches = matchRepository.findByPlayed(true);

        Map<Long, StandingsDTO> standingsMap = new HashMap<>();

        for (Equipo team : teams) {
            standingsMap.put(team.getId(), StandingsDTO.builder()
                    .teamId(team.getId())
                    .teamName(team.getNombre())
                    .played(0).won(0).drawn(0).lost(0)
                    .goalsFor(0).goalsAgainst(0).goalDifference(0).points(0)
                    .build());
        }

        for (Match match : matches) {
            if (!standingsMap.containsKey(match.getHomeTeam().getId()) ||
                !standingsMap.containsKey(match.getAwayTeam().getId())) continue;

            StandingsDTO h = standingsMap.get(match.getHomeTeam().getId());
            StandingsDTO a = standingsMap.get(match.getAwayTeam().getId());

            h.setPlayed(h.getPlayed() + 1);
            a.setPlayed(a.getPlayed() + 1);
            h.setGoalsFor(h.getGoalsFor() + match.getHomeGoals());
            h.setGoalsAgainst(h.getGoalsAgainst() + match.getAwayGoals());
            a.setGoalsFor(a.getGoalsFor() + match.getAwayGoals());
            a.setGoalsAgainst(a.getGoalsAgainst() + match.getHomeGoals());

            if (match.getHomeGoals() > match.getAwayGoals()) {
                h.setWon(h.getWon() + 1);
                h.setPoints(h.getPoints() + 3);
                a.setLost(a.getLost() + 1);
            } else if (match.getHomeGoals() < match.getAwayGoals()) {
                a.setWon(a.getWon() + 1);
                a.setPoints(a.getPoints() + 3);
                h.setLost(h.getLost() + 1);
            } else {
                h.setDrawn(h.getDrawn() + 1);
                h.setPoints(h.getPoints() + 1);
                a.setDrawn(a.getDrawn() + 1);
                a.setPoints(a.getPoints() + 1);
            }
        }

        List<StandingsDTO> sorted = new ArrayList<>(standingsMap.values());
        for (StandingsDTO dto : sorted) {
            dto.setGoalDifference(dto.getGoalsFor() - dto.getGoalsAgainst());
        }
        sorted.sort(Comparator.comparingInt(StandingsDTO::getPoints).reversed()
                .thenComparingInt(StandingsDTO::getGoalDifference).reversed()
                .thenComparingInt(StandingsDTO::getGoalsFor).reversed());

        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setPosition(i + 1);
        }

        return sorted;
    }
}
