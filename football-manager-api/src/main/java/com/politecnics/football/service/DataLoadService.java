package com.politecnics.football.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.repository.LigaRepository;
import com.politecnics.football.repository.MatchEventRepository;
import com.politecnics.football.repository.MatchRepository;
import com.politecnics.football.repository.JugadorRepository;
import com.politecnics.football.repository.EquipoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for loading and resetting LaLiga data from JSON.
 * Reads team, jugador, and match data from laliga_db.json and persists to DB.
 */
@Slf4j
@Service
public class DataLoadService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private LigaRepository ligaRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchEventRepository matchEventRepository;

    @Autowired
    private FixtureGenerator fixtureGenerator;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private EntityManager entityManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataLoadService() {
        objectMapper.findAndRegisterModules();
    }

    /**
     * Initializes data on application startup.
     * Only loads data if the database is empty (no teams found).
     */
    @PostConstruct
    @Transactional
    public void init() {
        try {
            loadData(false);
        } catch (Exception e) {
            log.error("Failed to initialize data on startup: {}", e.getMessage(), e);
            // Don't rethrow — allow app to start even if data load fails
            // User can trigger /api/league/reset to retry
        }
    }

    /**
     * Resets all league data: deletes everything and reloads from JSON.
     * Deletion order respects foreign key constraints:
     * match_events → matches → jugadores → teams → leagues
     */
    @Transactional
    public void resetLeagueData() {
        log.info("=== Resetting League Data (Native Truncate) ===");

        try {
            // Disable foreign key checks for a clean wipe
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

            log.info("Cleaning all tables...");
            String[] tables = {"match_events", "matches", "jugadores", "teams", "leagues", "seasons"};
            for (String table : tables) {
                try {
                    entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
                } catch (Exception e) {
                    log.warn("Could not truncate table '{}' (may not exist yet): {}", table, e.getMessage());
                }
            }

            // Re-enable foreign key checks
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

            // Clear persistence context
            entityManager.flush();
            entityManager.clear();

            log.info("All data wiped. Reloading from JSON...");
            loadData(true);
            log.info("=== League Reset Complete ===");
        } catch (Exception e) {
            log.error("Error during league reset: {}", e.getMessage(), e);
            throw new RuntimeException("Could not reset league data: " + e.getMessage(), e);
        }
    }

    /**
     * Loads LaLiga data from laliga_db.json.
     * @param force if true, loads even if data already exists
     */
    @Transactional
    public void loadData(boolean force) {
        if (!force && equipoRepository.count() > 0) {
            log.info("Data already loaded ({} teams found). Skipping.", equipoRepository.count());
            return;
        }

        try {
            log.info("Loading LaLiga data from JSON...");
            Resource resource = resourceLoader.getResource("classpath:data/laliga_db.json");

            InputStream inputStream;
            if (resource.exists()) {
                log.info("Found laliga_db.json in classpath");
                inputStream = resource.getInputStream();
            } else {
                // Fallback to file system path (during development)
                java.io.File file = new java.io.File("data/laliga_db.json");
                if (!file.exists()) {
                    file = new java.io.File("../data/laliga_db.json");
                }

                if (!file.exists()) {
                    log.warn("laliga_db.json not found in classpath or filesystem. Using FixtureGenerator fallback.");
                    List<Equipo> allTeams = equipoRepository.findAll();
                    if (allTeams.size() >= 2) {
                        fixtureGenerator.generateFixtures(allTeams);
                        log.info("Fallback fixtures generated for {} teams", allTeams.size());
                    } else {
                        log.error("No teams found and no JSON file available. Cannot initialize data.");
                    }
                    return;
                }

                log.info("Found laliga_db.json at filesystem path: {}", file.getAbsolutePath());
                inputStream = new java.io.FileInputStream(file);
            }

            Map<String, Object> data = objectMapper.readValue(inputStream, new TypeReference<>() {});

            // 1. Load or create League entity
            com.politecnics.football.entity.Liga league = ligaRepository.findAll().stream().findFirst().orElse(null);
            if (league == null) {
                league = com.politecnics.football.entity.Liga.builder()
                        .nombre((String) data.getOrDefault("leagueName", "LaLiga EA Sports"))
                        //.country((String) data.getOrDefault("country", "Spain"))
                        //.seasonYear(mapToInt(data.getOrDefault("seasonYear", 2025)))
                        //.managedTeamId(null)
                        .build();
                ligaRepository.save(league);
                log.info("League created: {}", league.getNombre());
            }

            // 2. Load Teams and Players
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> teamsData = (List<Map<String, Object>>) data.get("teams");
            int teamsLoaded = 0;
            int playersLoaded = 0;

            for (Map<String, Object> teamData : teamsData) {
                String teamId = (String) teamData.get("id");
                Equipo equipo = equipoRepository.findByTeamId(teamId).orElse(null);

                if (equipo == null) {
                    equipo = Equipo.builder()
                            .teamId(teamId)
                            .nombre((String) teamData.get("nombre"))
                            .estadio((String) teamData.get("stadium"))
                            .calidadRating(mapToInt(teamData.get("overallRating")))
                            .build();
                    equipo.setBudget(mapToLong(teamData.get("budget")));
                    teamsLoaded++;
                } else {
                    equipo.setNombre((String) teamData.get("nombre"));
                    equipo.setBudget(mapToLong(teamData.get("budget")));
                }
                equipo = equipoRepository.save(equipo);

                // Load Players for this team
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> playersData = (List<Map<String, Object>>) teamData.get("jugadores");
                if (playersData != null) {
                    for (Map<String, Object> playerData : playersData) {
                        String playerId = (String) playerData.get("id");
                        Jugador jugador = jugadorRepository.findByJugadorId(playerId).orElse(null);

                        if (jugador == null) {
                            jugador = Jugador.builder()
                                    .jugadorId(playerId)
                                    .nombre((String) playerData.get("name"))
                                    .posicion(com.politecnics.football.entity.Posicion.POR) // Default to compile
                                    .fechaNacimiento(java.time.LocalDate.now().minusYears(mapToInt(playerData.get("age"))))
                                    .calidad((double) mapToInt(playerData.get("overall")))
                                    .marketValue(mapToLong(playerData.getOrDefault("marketValue", 0)))
                                    .equipo(equipo)
                                    .goalsScored(0)
                                    .assists(0)
                                    .matchesPlayed(0)
                                    .yellowCards(0)
                                    .redCards(0)
                                    .build();
                            playersLoaded++;
                        } else {
                            jugador.setEquipo(equipo);
                        }
                        jugadorRepository.save(jugador);
                    }
                }
            }
            log.info("Teams loaded: {} new, {} total. Players loaded: {} new", teamsLoaded, teamsData.size(), playersLoaded);

            // 3. Load Matches (Schedule)
            boolean matchesLoaded = false;
            if (data.containsKey("matches")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> matchesData = (List<Map<String, Object>>) data.get("matches");
                if (matchesData != null && !matchesData.isEmpty()) {
                    log.info("Loading {} matches from JSON...", matchesData.size());
                    int matchCount = 0;
                    for (Map<String, Object> matchData : matchesData) {
                        String homeId = (String) matchData.get("homeTeamId");
                        String awayId = (String) matchData.get("awayTeamId");

                        Optional<Equipo> homeEquipo = equipoRepository.findByTeamId(homeId);
                        Optional<Equipo> awayEquipo = equipoRepository.findByTeamId(awayId);

                        if (homeEquipo.isPresent() && awayEquipo.isPresent()) {
                            String dateStr = (String) matchData.get("date");
                            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);

                            Match match = Match.builder()
                                    .matchday(mapToInt(matchData.get("matchday")))
                                    .homeTeam(homeEquipo.get())
                                    .awayTeam(awayEquipo.get())
                                    .homeGoals(mapToInt(matchData.get("homeGoals")))
                                    .awayGoals(mapToInt(matchData.get("awayGoals")))
                                    .played((Boolean) matchData.get("played"))
                                    .matchDate(dateTime.toLocalDate())
                                    .build();
                            matchRepository.save(match);
                            matchCount++;
                        } else {
                            log.warn("Skipping match: team not found (home={}, away={})", homeId, awayId);
                        }
                    }
                    if (matchCount > 0) {
                        matchesLoaded = true;
                        log.info("Matches loaded from JSON: {}", matchCount);
                    }
                }
            }

            // 4. Generate fixtures if no matches were in JSON
            if (!matchesLoaded && matchRepository.count() == 0) {
                log.info("No matches in JSON. Generating fixtures...");
                List<Equipo> allTeams = equipoRepository.findAll();
                fixtureGenerator.generateFixtures(allTeams);
                log.info("Fixtures generated. Total matches: {}", matchRepository.count());
            }

            log.info("=== LaLiga data loaded successfully ===");

        } catch (Exception e) {
            log.error("Failed to load LaLiga data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load LaLiga data: " + e.getMessage(), e);
        }
    }

    private Integer mapToInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            if (val instanceof String) return Integer.parseInt((String) val);
        } catch (NumberFormatException e) { /* ignored */ }
        return 0;
    }

    private Long mapToLong(Object val) {
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            if (val instanceof String) return Long.parseLong((String) val);
        } catch (NumberFormatException e) { /* ignored */ }
        return 0L;
    }
}
