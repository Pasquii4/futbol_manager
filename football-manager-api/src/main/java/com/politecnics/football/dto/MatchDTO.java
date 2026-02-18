package com.politecnics.football.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchDTO {
    private Long id;
    private Integer matchday;
    private Long homeTeamId;
    private String homeTeamName;
    private Long awayTeamId;
    private String awayTeamName;
    private Integer homeGoals;
    private Integer awayGoals;
    private boolean played;
    private LocalDate matchDate;
    @Builder.Default
    private List<MatchEventDTO> events = new ArrayList<>();
}
