package com.politecnics.football.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer matchday; // Jornada 1, 2, 3...
    
    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;
    
    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    private Integer homeGoals;
    private Integer awayGoals;
    
    @Column(name = "is_played")
    private boolean played; // True if match is finished

    private LocalDate matchDate;

    // Advanced stats
    private Integer possession; // Home possession percentage (0-100)

    @Column(name = "home_shots_on_target")
    private Integer homeShotsOnTarget;

    @Column(name = "away_shots_on_target")
    private Integer awayShotsOnTarget;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MatchEvent> events = new ArrayList<>();
}
