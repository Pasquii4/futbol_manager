package com.politecnics.football.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Player;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.LeagueRepository;
import com.politecnics.football.repository.PlayerRepository;
import com.politecnics.football.repository.TeamRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DataLoadService {

    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private LeagueRepository leagueRepository;

    @Autowired
    private com.politecnics.football.repository.MatchRepository matchRepository;
    
    @Autowired
    private FixtureGenerator fixtureGenerator;

    @Autowired
    private ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataLoadService() {
        objectMapper.findAndRegisterModules();
    }

    @PostConstruct
    @Transactional
    public void init() {
        loadData(false);
    }

    private void log(String msg) {
        try {
            java.nio.file.Files.writeString(java.nio.file.Path.of("loading.log"), 
                java.time.LocalDateTime.now() + ": " + msg + "\n", 
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {}
    }

    @Transactional
    public void resetLeagueData() {
        log("Resetting League Data...");
        System.out.println("Resetting League Data...");
        matchRepository.deleteAll();
        playerRepository.deleteAll();
        teamRepository.deleteAll();
        leagueRepository.deleteAll(); // Optional, if you want to reset League entity too
        
        loadData(true);
    }

    @Transactional
    public void loadData(boolean force) {
        if (!force && teamRepository.count() > 0) {
            System.out.println("Data already loaded. Skipping.");
            return;
        }

        try {
            log("Loading LaLiga data from JSON...");
            System.out.println("Loading LaLiga data from JSON...");
            Resource resource = resourceLoader.getResource("classpath:data/laliga_db.json"); // Assuming file is in src/main/resources/data/
            
            // Fallback to file system if not found in classpath (during dev)
            InputStream inputStream;
            if (resource.exists()) {
                inputStream = resource.getInputStream();
            } else {
                // Try file system path
                // Try file system path (Root vs API folder)
                java.io.File file = new java.io.File("data/laliga_db.json");
                if (!file.exists()) {
                    file = new java.io.File("../data/laliga_db.json");
                }
                
                if (!file.exists()) {
                    System.err.println("laliga_db.json not found in classpath or filesystem (data/ or ../data/)!");
                    // Don't return, let it fail or handle? 
                    // Use fallback generator?
                    System.out.println("Using FixtureGenerator as fallback...");
                     List<Team> allTeams = teamRepository.findAll();
                     if (allTeams.size() >= 2) {
                        fixtureGenerator.generateFixtures(allTeams);
                     }
                    return;
                }
                if (!file.exists()) {
                    log("laliga_db.json not found in classpath or filesystem!");
                    System.err.println("laliga_db.json not found in classpath or filesystem (data/ or ../data/)!");
                    // Don't return, let it fail or handle? 
                    // Use fallback generator?
                    System.out.println("Using FixtureGenerator as fallback...");
                     List<Team> allTeams = teamRepository.findAll();
                     log("Falling back for teams: " + allTeams.size());
                     if (allTeams.size() >= 2) {
                        fixtureGenerator.generateFixtures(allTeams);
                     }
                    return;
                }
                inputStream = new java.io.FileInputStream(file);
            }
            log("File found, parsing...");

            Map<String, Object> data = objectMapper.readValue(inputStream, new TypeReference<>() {});

            // Load League info if needed
            // Load League info if needed
            com.politecnics.football.entity.League league = leagueRepository.findAll().stream().findFirst().orElse(null);
            if (league == null) {
                league = com.politecnics.football.entity.League.builder()
                        .name((String) data.getOrDefault("leagueName", "LaLiga EA Sports"))
                        .country((String) data.getOrDefault("country", "Spain"))
                        .seasonYear(mapToInt(data.getOrDefault("seasonYear", 2025)))
                        .managedTeamId(null)
                        .build();
                leagueRepository.save(league);
            }

                // Load Teams
                List<Map<String, Object>> teamsData = (List<Map<String, Object>>) data.get("teams");
                for (Map<String, Object> teamData : teamsData) {
                    String teamId = (String) teamData.get("id");
                    Team team = teamRepository.findByTeamId(teamId).orElse(null);

                    if (team == null) {
                        team = Team.builder()
                                .teamId(teamId)
                                .name((String) teamData.get("name"))
                                .stadium((String) teamData.get("stadium"))
                                .budget(mapToLong(teamData.get("budget")))
                                .overallRating(mapToInt(teamData.get("overallRating")))
                                .build();
                    } else {
                        // Update existing if needed (optional)
                        team.setName((String) teamData.get("name"));
                        team.setBudget(mapToLong(teamData.get("budget")));
                    }
                    team = teamRepository.save(team);

                    // Load Players
                    List<Map<String, Object>> playersData = (List<Map<String, Object>>) teamData.get("players");
                    if (playersData != null) {
                        for (Map<String, Object> playerData : playersData) {
                            String playerId = (String) playerData.get("id");
                            Player player = playerRepository.findByPlayerId(playerId).orElse(null);

                            if (player == null) {
                                player = Player.builder()
                                        .playerId(playerId)
                                        .name((String) playerData.get("name"))
                                        .position((String) playerData.get("position"))
                                        .age((Integer) playerData.get("age"))
                                        .overall((Integer) playerData.get("overall"))
                                        .potential((Integer) playerData.get("potential"))
                                        .team(team)
                                        .goalsScored(0)
                                        .assists(0)
                                        .matchesPlayed(0)
                                        .yellowCards(0)
                                        .redCards(0)
                                        .build();
                            } else {
                                // Update player team ref just in case
                                player.setTeam(team);
                            }
                            playerRepository.save(player);
                        }
                    }
                }

            System.out.println("LaLiga data loaded successfully!");
            log("Teams loaded. Count: " + teamsData.size());
            
            // Load Matches (Schedule)
            boolean matchesLoaded = false;
            if (data.containsKey("matches")) {
                List<Map<String, Object>> matchesData = (List<Map<String, Object>>) data.get("matches");
                if (matchesData != null && !matchesData.isEmpty()) {
                    System.out.println("Loading matches from JSON...");
                    for (Map<String, Object> matchData : matchesData) {
                        String homeId = (String) matchData.get("homeTeamId");
                        String awayId = (String) matchData.get("awayTeamId");
                        
                        Optional<Team> homeTeam = teamRepository.findByTeamId(homeId);
                        Optional<Team> awayTeam = teamRepository.findByTeamId(awayId);
                        
                        if (homeTeam.isPresent() && awayTeam.isPresent()) {
                             String dateStr = (String) matchData.get("date");
                             LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
                             
                             Match match = Match.builder()
                                     .matchday(mapToInt(matchData.get("matchday")))
                                     .homeTeam(homeTeam.get())
                                     .awayTeam(awayTeam.get())
                                     .homeGoals(mapToInt(matchData.get("homeGoals")))
                                     .awayGoals(mapToInt(matchData.get("awayGoals")))
                                     .played((Boolean) matchData.get("played"))
                                     .matchDate(dateTime.toLocalDate())
                                     .build();
                             matchRepository.save(match);
                        }
                    }
                    if (matchRepository.count() > 0) {
                        matchesLoaded = true;
                        System.out.println("Matches loaded successfully!");
                        log("Matches loaded from JSON. Count: " + matchRepository.count());
                    }
                }
            }
            
            if (!matchesLoaded && matchRepository.count() == 0) {
                log("No matches in JSON. Generating fixtures...");
                System.out.println("No matches found in JSON. Generating fixtures...");
                List<Team> allTeams = teamRepository.findAll();
                fixtureGenerator.generateFixtures(allTeams);
                log("Fixtures generated. New match count: " + matchRepository.count());
            }

        } catch (Exception e) {
            e.printStackTrace();
            log("ERROR: " + e.getMessage());
            // Log to file to be sure
            try {
                java.nio.file.Files.writeString(java.nio.file.Path.of("init_error.log"), e.toString() + "\n" + java.util.Arrays.toString(e.getStackTrace()));
            } catch (IOException io) {}
            throw new RuntimeException("Failed to load LaLiga data: " + e.getMessage());
        }
    }

    private Integer mapToInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        try {
            if (val instanceof String) return Integer.parseInt((String) val);
        } catch (NumberFormatException e) {}
        return 0;
    }

    private Long mapToLong(Object val) {
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            if (val instanceof String) return Long.parseLong((String) val);
        } catch (NumberFormatException e) {}
        return 0L;
    }
}
