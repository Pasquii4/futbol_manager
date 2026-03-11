package com.politecnics.football.mapper;

import com.politecnics.football.dto.MatchDTO;
import com.politecnics.football.dto.MatchEventDTO;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    @Mapping(source = "homeTeam.id", target = "homeTeamId")
    @Mapping(source = "homeTeam.nombre", target = "homeTeamName")
    @Mapping(source = "awayTeam.id", target = "awayTeamId")
    @Mapping(source = "awayTeam.nombre", target = "awayTeamName")
    MatchDTO toDTO(Match match);

    @Mapping(source = "jugador.id", target = "playerId")
    @Mapping(source = "jugador.nombre", target = "playerName")
    @Mapping(source = "jugador.equipo.id", target = "teamId")
    MatchEventDTO toEventDTO(MatchEvent event);
}
