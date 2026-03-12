package com.politecnics.football.service;

import com.politecnics.football.repository.JugadorRepository;
import com.politecnics.football.repository.EquipoRepository;
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
    private EquipoRepository equipoRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Test
    void testDataLoading() {
        // DataLoadService @PostConstruct should have run
        
        long teamCount = equipoRepository.count();
        long playerCount = jugadorRepository.count();

        System.out.println("Teams loaded: " + teamCount);
        System.out.println("Players loaded: " + playerCount);

        assertTrue(teamCount > 0, "Teams should be loaded");
        assertTrue(playerCount > 0, "Players should be loaded");

        // Check Real Madrid
        var realMadrid = equipoRepository.findByTeamId("real_madrid");
        assertTrue(realMadrid.isPresent());
        assertEquals("Real Madrid CF", realMadrid.get().getNombre());
        assertFalse(realMadrid.get().getJugadores().isEmpty());

        // Verify some players and their positions
        var players = realMadrid.get().getJugadores();
        boolean hasGk = players.stream().anyMatch(p -> p.getPosicion() == com.politecnics.football.entity.Posicion.POR);
        boolean hasDef = players.stream().anyMatch(p -> p.getPosicion() == com.politecnics.football.entity.Posicion.DEF);
        boolean hasMid = players.stream().anyMatch(p -> p.getPosicion() == com.politecnics.football.entity.Posicion.MIG);
        boolean hasDav = players.stream().anyMatch(p -> p.getPosicion() == com.politecnics.football.entity.Posicion.DAV);

        assertTrue(hasGk, "Should have goalkeepers");
        assertTrue(hasDef, "Should have defenders");
        assertTrue(hasMid, "Should have midfielders");
        assertTrue(hasDav, "Should have forwards");
    }
}
