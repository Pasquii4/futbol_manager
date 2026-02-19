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

    // Stats fields can be added here or in a separate SeasonStats entity
    private Integer goalsScored = 0;
    private Integer assists = 0;
    private Integer yellowCards = 0;
    private Integer redCards = 0;
    private Integer matchesPlayed = 0;
}
