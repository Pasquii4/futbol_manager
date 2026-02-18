package com.politecnics.football.service;

import com.politecnics.football.repository.PlayerRepository;
import com.politecnics.football.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class DataLoadServiceTest {

    @Autowired
    private DataLoadService dataLoadService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void testDataLoading() {
        // DataLoadService @PostConstruct should have run
        
        long teamCount = teamRepository.count();
        long playerCount = playerRepository.count();

        System.out.println("Teams loaded: " + teamCount);
        System.out.println("Players loaded: " + playerCount);

        assertTrue(teamCount > 0, "Teams should be loaded");
        assertTrue(playerCount > 0, "Players should be loaded");

        // Check Real Madrid
        var realMadrid = teamRepository.findByTeamId("real_madrid");
        assertTrue(realMadrid.isPresent());
        assertEquals("Real Madrid", realMadrid.get().getName());
        assertFalse(realMadrid.get().getPlayers().isEmpty());
    }
}
