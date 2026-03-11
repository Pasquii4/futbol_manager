package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Stores historical season data: champion, top scorer, and standings snapshot.
 */
@Entity
@Table(name = "seasons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season_year")
    private Integer seasonYear;

    private String champion;

    @Column(name = "top_scorer")
    private String topScorer;

    @Column(name = "top_scorer_goals")
    private Integer topScorerGoals;

    @Column(name = "standings_snapshot", columnDefinition = "TEXT")
    private String standingsSnapshot; // JSON snapshot of final standings
}
