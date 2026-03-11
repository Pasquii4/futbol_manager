package com.politecnics.football.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeagueStatusDTO {
    private Integer currentMatchday;
    private Integer totalMatchdays;
    private Integer seasonYear;
    @JsonProperty("isStarted")
    private boolean isStarted;
    @JsonProperty("isFinished")
    private boolean isFinished;
    private Long managedTeamId;
}
