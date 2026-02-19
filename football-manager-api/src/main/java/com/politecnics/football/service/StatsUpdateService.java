package com.politecnics.football.service;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.Player;
import com.politecnics.football.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatsUpdateService {

    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public void updateStatsCoordinates(Match match) {
        // Update Matches Played (Simplified: All players in team? No, we don't have lineups yet)
        // For MVP, we only update players who have events or we should ideally have a lineup.
        // Since we don't have lineups, maybe we skip matchesPlayed or assume 11 randoms?
        // Let's just update goalscorers/carded players for now to be safe, 
        // OR better: Update matchesPlayed for players involved in events.
        
        // Actually, let's just process events.
        if (match.getEvents() != null) {
            for (MatchEvent event : match.getEvents()) {
                Player player = event.getPlayer();
                if (player != null) {
                    switch (event.getType()) {
                        case GOAL:
                            player.setGoalsScored(player.getGoalsScored() + 1);
                            break;
                        case ASSIST: // If we had assists
                            player.setAssists(player.getAssists() + 1);
                            break;
                        case YELLOW_CARD:
                            player.setYellowCards(player.getYellowCards() + 1);
                            break;
                        case RED_CARD:
                            player.setRedCards(player.getRedCards() + 1);
                            break;
                    }
                    // Generic "Played" increment? 
                    // If we increment here, a player with 2 goals gets 2 matches played? No.
                    // We should track unique players.
                }
            }
        }
        
        // TODO: In future, when lineups exist, iterate lineup and increment matchesPlayed.
    }
}
