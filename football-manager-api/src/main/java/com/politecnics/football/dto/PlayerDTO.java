package com.politecnics.football.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDTO {
    private Long id;
    private String playerId;
    private String name;
    private String position;
    private Integer age;
    private Integer overall;
    private Integer potential;
    private Integer goalsScored;
    private Integer assists;
    private Integer yellowCards;
    private Integer redCards;
    private Long marketValue;
    private Integer matchesPlayed;
    private String teamId;
    private String teamName;
}
