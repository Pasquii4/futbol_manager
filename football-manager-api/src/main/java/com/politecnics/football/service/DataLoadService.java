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

    @PostConstruct
    @Transactional
    public void loadData() {
        if (teamRepository.count() > 0) {
            System.out.println("Data already loaded. Skipping.");
            return;
        }

        try {
            System.out.println("Loading LaLiga data from JSON...");
            Resource resource = resourceLoader.getResource("classpath:data/laliga_db.json"); // Assuming file is in src/main/resources/data/
            
            // Fallback to file system if not found in classpath (during dev)
            InputStream inputStream;
            if (resource.exists()) {
                inputStream = resource.getInputStream();
            } else {
                // Try file system path
                java.io.File file = new java.io.File("data/laliga_db.json");
                if (!file.exists()) {
                    System.err.println("laliga_db.json not found!");
                    return;
                }
                inputStream = new java.io.FileInputStream(file);
            }

            Map<String, Object> data = objectMapper.readValue(inputStream, new TypeReference<>() {});

            // Load League info if needed
            // ...

            // Load Teams
            List<Map<String, Object>> teamsData = (List<Map<String, Object>>) data.get("teams");
            for (Map<String, Object> teamData : teamsData) {
                Team team = Team.builder()
                        .teamId((String) teamData.get("id"))
                        .name((String) teamData.get("name"))
                        .stadium((String) teamData.get("stadium"))
                        // Handle Long safely
                        .budget(((Number) teamData.get("budget")).longValue())
                        .overallRating((Integer) teamData.get("overallRating"))
                        .build();

                team = teamRepository.save(team);

                // Load Players
                List<Map<String, Object>> playersData = (List<Map<String, Object>>) teamData.get("players");
                for (Map<String, Object> playerData : playersData) {
                    Player player = Player.builder()
                            .playerId((String) playerData.get("id"))
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
                    
                    playerRepository.save(player);
                }
            }
            System.out.println("LaLiga data loaded successfully!");
            
            // Load Matches (Schedule)
            if (data.containsKey("matches")) {
                List<Map<String, Object>> matchesData = (List<Map<String, Object>>) data.get("matches");
                if (matchRepository.count() == 0 && matchesData != null && !matchesData.isEmpty()) {
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
                                     .matchday((Integer) matchData.get("matchday"))
                                     .homeTeam(homeTeam.get())
                                     .awayTeam(awayTeam.get())
                                     .homeGoals((Integer) matchData.get("homeGoals"))
                                     .awayGoals((Integer) matchData.get("awayGoals"))
                                     .played((Boolean) matchData.get("played"))
                                     .matchDate(dateTime.toLocalDate())
                                     .build();
                             matchRepository.save(match);
                        }
                    }
                    System.out.println("Matches loaded successfully!");
                }
            } else if (matchRepository.count() == 0) {
                System.out.println("No matches found in JSON. Generating fixtures...");
                List<Team> allTeams = teamRepository.findAll();
                fixtureGenerator.generateFixtures(allTeams);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load LaLiga data: " + e.getMessage());
        }
    }
}
