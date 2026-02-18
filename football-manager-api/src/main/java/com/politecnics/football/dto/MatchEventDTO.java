package com.politecnics.football.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchEventDTO {
    private Long id;
    private Long playerId;
    private String playerName;
    private String type; // GOAL, CARD...
    private Integer minute;
}
