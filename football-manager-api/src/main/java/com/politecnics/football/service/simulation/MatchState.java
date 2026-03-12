package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MatchState {
    private final Match match;
    private int currentMinute = 0;
    private int homeGoals = 0;
    private int awayGoals = 0;
    private boolean homeHasRedCard = false;
    private boolean awayHasRedCard = false;
    private int homeSubstitutionsUsed = 0;
    private int awaySubstitutionsUsed = 0;
    private double homeXG = 0.0;
    private double awayXG = 0.0;
    private int homeTotalShots = 0;
    private int awayTotalShots = 0;
    private int homeShotsOnTarget = 0;
    private int awayShotsOnTarget = 0;
    private final List<MatchEvent> events = new ArrayList<>();
    
    // Helper
    public int getScoreDiff(boolean isHome) {
        return isHome ? homeGoals - awayGoals : awayGoals - homeGoals;
    }
}
