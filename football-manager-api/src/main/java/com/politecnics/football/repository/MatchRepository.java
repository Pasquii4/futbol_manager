package com.politecnics.football.repository;

import com.politecnics.football.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByMatchday(Integer matchday);
    List<Match> findByPlayed(boolean played);
    
    // Find the first matchday that has at least one unplayed match
    Match findFirstByPlayedFalseOrderByMatchdayAsc();
}
