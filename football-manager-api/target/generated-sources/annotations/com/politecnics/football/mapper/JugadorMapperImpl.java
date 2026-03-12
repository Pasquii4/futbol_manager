package com.politecnics.football.mapper;

import com.politecnics.football.dto.EstadisticasJugadorDTO;
import com.politecnics.football.dto.LesionDTO;
import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.entity.EstadisticasJugador;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Lesion;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-12T12:56:22+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (JetBrains s.r.o.)"
)
@Component
public class JugadorMapperImpl implements JugadorMapper {

    @Override
    public PlayerDTO toDTO(Jugador jugador) {
        if ( jugador == null ) {
            return null;
        }

        PlayerDTO.PlayerDTOBuilder playerDTO = PlayerDTO.builder();

        playerDTO.id( jugador.getId() );
        playerDTO.goalsScored( jugador.getGoalsScored() );
        playerDTO.assists( jugador.getAssists() );
        playerDTO.yellowCards( jugador.getYellowCards() );
        playerDTO.redCards( jugador.getRedCards() );
        playerDTO.marketValue( jugador.getMarketValue() );
        playerDTO.matchesPlayed( jugador.getMatchesPlayed() );

        return playerDTO.build();
    }

    @Override
    public Jugador toEntity(PlayerDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Jugador.JugadorBuilder jugador = Jugador.builder();

        jugador.id( dto.getId() );
        jugador.marketValue( dto.getMarketValue() );
        jugador.goalsScored( dto.getGoalsScored() );
        jugador.assists( dto.getAssists() );
        jugador.yellowCards( dto.getYellowCards() );
        jugador.redCards( dto.getRedCards() );
        jugador.matchesPlayed( dto.getMatchesPlayed() );

        return jugador.build();
    }

    @Override
    public EstadisticasJugadorDTO toEstadisticasDTO(EstadisticasJugador estadisticas) {
        if ( estadisticas == null ) {
            return null;
        }

        Integer goles = null;
        Integer asistencias = null;
        Integer tarjetasAmarillas = null;
        Integer tarjetasRojas = null;
        Integer partidosJugados = null;
        Integer minutosJugados = null;
        Double ratingPromedio = null;
        Integer paradas = null;
        Integer golesRecibidos = null;

        goles = estadisticas.getGoles();
        asistencias = estadisticas.getAsistencias();
        tarjetasAmarillas = estadisticas.getTarjetasAmarillas();
        tarjetasRojas = estadisticas.getTarjetasRojas();
        partidosJugados = estadisticas.getPartidosJugados();
        minutosJugados = estadisticas.getMinutosJugados();
        ratingPromedio = estadisticas.getRatingPromedio();
        paradas = estadisticas.getParadas();
        golesRecibidos = estadisticas.getGolesRecibidos();

        EstadisticasJugadorDTO estadisticasJugadorDTO = new EstadisticasJugadorDTO( goles, asistencias, tarjetasAmarillas, tarjetasRojas, partidosJugados, minutosJugados, ratingPromedio, paradas, golesRecibidos );

        return estadisticasJugadorDTO;
    }

    @Override
    public EstadisticasJugador toEstadisticasEntity(EstadisticasJugadorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        EstadisticasJugador.EstadisticasJugadorBuilder estadisticasJugador = EstadisticasJugador.builder();

        estadisticasJugador.goles( dto.goles() );
        estadisticasJugador.asistencias( dto.asistencias() );
        estadisticasJugador.tarjetasAmarillas( dto.tarjetasAmarillas() );
        estadisticasJugador.tarjetasRojas( dto.tarjetasRojas() );
        estadisticasJugador.partidosJugados( dto.partidosJugados() );
        estadisticasJugador.minutosJugados( dto.minutosJugados() );
        estadisticasJugador.ratingPromedio( dto.ratingPromedio() );
        estadisticasJugador.paradas( dto.paradas() );
        estadisticasJugador.golesRecibidos( dto.golesRecibidos() );

        return estadisticasJugador.build();
    }

    @Override
    public LesionDTO toLesionDTO(Lesion lesion) {
        if ( lesion == null ) {
            return null;
        }

        Long id = null;
        Integer diasRestantes = null;
        String tipoLesion = null;

        id = lesion.getId();
        diasRestantes = lesion.getDiasRestantes();
        tipoLesion = lesion.getTipoLesion();

        LesionDTO lesionDTO = new LesionDTO( id, diasRestantes, tipoLesion );

        return lesionDTO;
    }

    @Override
    public Lesion toLesionEntity(LesionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Lesion.LesionBuilder lesion = Lesion.builder();

        lesion.id( dto.id() );
        lesion.diasRestantes( dto.diasRestantes() );
        lesion.tipoLesion( dto.tipoLesion() );

        return lesion.build();
    }
}
