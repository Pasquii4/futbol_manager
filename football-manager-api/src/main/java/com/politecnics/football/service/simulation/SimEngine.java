package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Match;

public interface SimEngine {
    MatchResult simulate(Match match);
}
