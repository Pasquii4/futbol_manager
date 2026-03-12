package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.MatchEvent;
import com.politecnics.football.entity.MatchStats;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchResult {
    private int homeGoals;
    private int awayGoals;
    private List<MatchEvent> events;
    private MatchStats stats;
}
