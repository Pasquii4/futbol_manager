package com.politecnics.football.repository;

import com.politecnics.football.entity.MatchStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchStatsRepository extends JpaRepository<MatchStats, Long> {
    MatchStats findByMatchId(Long matchId);
}
