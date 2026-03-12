package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.MatchStats;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.repository.MatchEventRepository;
import com.politecnics.football.repository.MatchStatsRepository;
import com.politecnics.football.repository.JugadorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class MatchEngine {

    @Autowired private FullSimEngine fullSimEngine;
    @Autowired private FastSimEngine fastSimEngine;
    @Autowired private MatchStatsRepository matchStatsRepository;
    @Autowired private MatchEventRepository matchEventRepository;
    @Autowired private JugadorRepository jugadorRepository;

    private final Random random = new Random();
    private static final double FORM_WIN_BOOST = 0.3;
    private static final double FORM_LOSS_PENALTY = -0.4;
    private static final double FORM_DRAW_CHANGE = 0.05;

    /**
     * Punto de entrada principal.
     * userTeamId: el equipo del usuario. Si alguno de los equipos lo es → FullSim.
     */
    public MatchResult simulateMatch(Match match, Long userTeamId) {
        SimEngine engine = shouldUseFull(match, userTeamId) ? fullSimEngine : fastSimEngine;
        
        MatchResult result = engine.simulate(match);
        
        // Persistir resultado en Match
        match.setHomeGoals(result.getHomeGoals());
        match.setAwayGoals(result.getAwayGoals());
        match.setPlayed(true);
        
        // Sincronizar stats básicas en la entidad Match para compatibilidad
        if (result.getStats() != null) {
            match.setPossession(result.getStats().getHomePossession());
            match.setHomeShotsOnTarget(result.getStats().getHomeShotsOnTarget());
            match.setAwayShotsOnTarget(result.getStats().getAwayShotsOnTarget());
        }
        
        // Persistir estadísticas avanzadas
        MatchStats stats = result.getStats();
        if (stats != null) {
            stats.setMatch(match);
            matchStatsRepository.save(stats);
        }
        
        // Persistir eventos
        if (result.getEvents() != null) {
            result.getEvents().forEach(e -> {
                e.setMatch(match);
                matchEventRepository.save(e);
            });
            match.setEvents(result.getEvents());
        }
        
        // Actualizar forma y curar lesiones (las lesiones nuevas se generan por evento)
        updatePlayerForm(match, result);
        healInjuredPlayers(match.getHomeTeam());
        healInjuredPlayers(match.getAwayTeam());
        
        return result;
    }
    
    private boolean shouldUseFull(Match match, Long userTeamId) {
        if (userTeamId == null) return false;
        return match.getHomeTeam().getId().equals(userTeamId) || 
               match.getAwayTeam().getId().equals(userTeamId);
    }
    
    private void updatePlayerForm(Match match, MatchResult result) {
        int homeGoals = result.getHomeGoals();
        int awayGoals = result.getAwayGoals();
        
        updateTeamForm(match.getHomeTeam(), homeGoals > awayGoals ? 1 : homeGoals < awayGoals ? -1 : 0);
        updateTeamForm(match.getAwayTeam(), awayGoals > homeGoals ? 1 : awayGoals < homeGoals ? -1 : 0);
    }
    
    private void updateTeamForm(Equipo team, int result) {
        if (team.getJugadores() == null) return;
        double formChange = (result > 0) ? FORM_WIN_BOOST : (result < 0) ? FORM_LOSS_PENALTY : FORM_DRAW_CHANGE;
        for (Jugador jugador : team.getJugadores()) {
            double currentForm = jugador.getForma() != null ? jugador.getForma() : 7.0;
            double newForm = Math.min(10.0, Math.max(1.0, currentForm + formChange + (random.nextDouble() * 0.4 - 0.2)));
            jugador.setForma(newForm);
            jugadorRepository.save(jugador);
        }
    }
    
    private void healInjuredPlayers(Equipo team) {
        if (team.getJugadores() == null) return;
        for (Jugador jugador : team.getJugadores()) {
            if (jugador.getInjured() != null && jugador.getInjured()) {
                int remaining = jugador.getInjuryDuration() != null ? jugador.getInjuryDuration() - 1 : 0;
                if (remaining <= 0) {
                    jugador.setInjured(false);
                    jugador.setInjuryDuration(0);
                } else {
                    jugador.setInjuryDuration(remaining);
                }
                jugadorRepository.save(jugador);
            }
        }
    }
}
