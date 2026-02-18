package com.politecnics.football.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDTO {
    private Long id;
    private String teamId;
    private String name;
    private String stadium;
    private Long budget;
    private Integer overallRating;
    private List<PlayerDTO> players;
}
