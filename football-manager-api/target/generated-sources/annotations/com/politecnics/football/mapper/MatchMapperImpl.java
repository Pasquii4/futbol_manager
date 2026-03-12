package com.politecnics.football.mapper;

import com.politecnics.football.dto.MatchDTO;
import com.politecnics.football.dto.MatchEventDTO;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.MatchEvent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-12T12:56:22+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (JetBrains s.r.o.)"
)
@Component
public class MatchMapperImpl implements MatchMapper {

    @Override
    public MatchDTO toDTO(Match match) {
        if ( match == null ) {
            return null;
        }

        MatchDTO.MatchDTOBuilder matchDTO = MatchDTO.builder();

        matchDTO.homeTeamId( matchHomeTeamId( match ) );
        matchDTO.homeTeamName( matchHomeTeamNombre( match ) );
        matchDTO.awayTeamId( matchAwayTeamId( match ) );
        matchDTO.awayTeamName( matchAwayTeamNombre( match ) );
        matchDTO.id( match.getId() );
        matchDTO.matchday( match.getMatchday() );
        matchDTO.homeGoals( match.getHomeGoals() );
        matchDTO.awayGoals( match.getAwayGoals() );
        matchDTO.played( match.isPlayed() );
        matchDTO.matchDate( match.getMatchDate() );
        matchDTO.events( matchEventListToMatchEventDTOList( match.getEvents() ) );

        return matchDTO.build();
    }

    @Override
    public MatchEventDTO toEventDTO(MatchEvent event) {
        if ( event == null ) {
            return null;
        }

        MatchEventDTO.MatchEventDTOBuilder matchEventDTO = MatchEventDTO.builder();

        matchEventDTO.playerId( eventJugadorId( event ) );
        matchEventDTO.playerName( eventJugadorNombre( event ) );
        matchEventDTO.teamId( eventJugadorEquipoId( event ) );
        matchEventDTO.id( event.getId() );
        if ( event.getType() != null ) {
            matchEventDTO.type( event.getType().name() );
        }
        matchEventDTO.minute( event.getMinute() );

        return matchEventDTO.build();
    }

    private Long matchHomeTeamId(Match match) {
        if ( match == null ) {
            return null;
        }
        Equipo homeTeam = match.getHomeTeam();
        if ( homeTeam == null ) {
            return null;
        }
        Long id = homeTeam.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String matchHomeTeamNombre(Match match) {
        if ( match == null ) {
            return null;
        }
        Equipo homeTeam = match.getHomeTeam();
        if ( homeTeam == null ) {
            return null;
        }
        String nombre = homeTeam.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Long matchAwayTeamId(Match match) {
        if ( match == null ) {
            return null;
        }
        Equipo awayTeam = match.getAwayTeam();
        if ( awayTeam == null ) {
            return null;
        }
        Long id = awayTeam.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String matchAwayTeamNombre(Match match) {
        if ( match == null ) {
            return null;
        }
        Equipo awayTeam = match.getAwayTeam();
        if ( awayTeam == null ) {
            return null;
        }
        String nombre = awayTeam.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    protected List<MatchEventDTO> matchEventListToMatchEventDTOList(List<MatchEvent> list) {
        if ( list == null ) {
            return null;
        }

        List<MatchEventDTO> list1 = new ArrayList<MatchEventDTO>( list.size() );
        for ( MatchEvent matchEvent : list ) {
            list1.add( toEventDTO( matchEvent ) );
        }

        return list1;
    }

    private Long eventJugadorId(MatchEvent matchEvent) {
        if ( matchEvent == null ) {
            return null;
        }
        Jugador jugador = matchEvent.getJugador();
        if ( jugador == null ) {
            return null;
        }
        Long id = jugador.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String eventJugadorNombre(MatchEvent matchEvent) {
        if ( matchEvent == null ) {
            return null;
        }
        Jugador jugador = matchEvent.getJugador();
        if ( jugador == null ) {
            return null;
        }
        String nombre = jugador.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Long eventJugadorEquipoId(MatchEvent matchEvent) {
        if ( matchEvent == null ) {
            return null;
        }
        Jugador jugador = matchEvent.getJugador();
        if ( jugador == null ) {
            return null;
        }
        Equipo equipo = jugador.getEquipo();
        if ( equipo == null ) {
            return null;
        }
        Long id = equipo.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
