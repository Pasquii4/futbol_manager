package com.politecnics.football.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String playerId; // String ID from JSON

    private String name;
    private String position;
    private Integer age;
    private Integer overall;
    private Integer potential;
    
    @Column(name = "market_value")
    private Long marketValue; // Value in Euro


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnore // Prevent infinite recursion in default serialization
    private Team team;

    // Stats fields
    @Builder.Default
    private Integer goalsScored = 0;
    @Builder.Default
    private Integer assists = 0;
    @Builder.Default
    private Integer yellowCards = 0;
    @Builder.Default
    private Integer redCards = 0;
    @Builder.Default
    private Integer matchesPlayed = 0;

    // Simulation fields
    @Column(name = "injured")
    @Builder.Default
    private Boolean injured = false;

    @Column(name = "injury_duration")
    @Builder.Default
    private Integer injuryDuration = 0; // Matchdays remaining

    @Builder.Default
    private Double form = 7.0; // 1-10 scale, affects performance
}
