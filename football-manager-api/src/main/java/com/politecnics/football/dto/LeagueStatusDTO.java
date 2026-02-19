package com.politecnics.football.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeagueStatusDTO {
    private Integer currentMatchday;
    private Integer totalMatchdays;
    private Integer seasonYear;
    private boolean isStarted;
    private boolean isFinished;
    private Long managedTeamId;
}
