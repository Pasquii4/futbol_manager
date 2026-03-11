package com.politecnics.football.service;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatsUpdateService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Transactional
    public void updateStatsCoordinates(Match match) {
        // Update Matches Played (Simplified: All jugadores in team? No, we don't have lineups yet)
        // For MVP, we only update jugadores who have events or we should ideally have a lineup.
        // Since we don't have lineups, maybe we skip matchesPlayed or assume 11 randoms?
        // Let's just update goalscorers/carded jugadores for now to be safe, 
        // OR better: Update matchesPlayed for jugadores involved in events.
        
        // Actually, let's just process events.
        if (match.getEvents() != null) {
            for (MatchEvent event : match.getEvents()) {
                Jugador jugador = event.getJugador();
                if (jugador != null) {
                    switch (event.getType()) {
                        case GOAL:
                            jugador.setGoalsScored(jugador.getGoalsScored() + 1);
                            break;
                        case ASSIST: // If we had assists
                            jugador.setAssists(jugador.getAssists() + 1);
                            break;
                        case YELLOW_CARD:
                            jugador.setYellowCards(jugador.getYellowCards() + 1);
                            break;
                        case RED_CARD:
                            jugador.setRedCards(jugador.getRedCards() + 1);
                            break;
                    }
                    // Generic "Played" increment? 
                    // If we increment here, a jugador with 2 goals gets 2 matches played? No.
                    // We should track unique jugadores.
                }
            }
        }
        
        // TODO: In future, when lineups exist, iterate lineup and increment matchesPlayed.
    }
}
