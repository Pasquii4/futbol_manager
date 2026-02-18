package com.politecnics.football.repository;

import com.politecnics.football.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByTeam_TeamId(String teamId);
    
    // Stats
    List<Player> findTop20ByOrderByGoalsScoredDesc();
    List<Player> findTop20ByOrderByAssistsDesc();
    List<Player> findTop20ByOrderByOverallDesc();
}
