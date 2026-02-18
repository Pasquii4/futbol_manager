package com.politecnics.football.mapper;

import com.politecnics.football.dto.EventoPartidoDTO;
import com.politecnics.football.dto.PartidoDTO;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.EventoPartido;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Partido;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-18T11:04:22+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Eclipse Adoptium)"
)
@Component
public class PartidoMapperImpl implements PartidoMapper {

    @Override
    public PartidoDTO toDTO(Partido partido) {
        if ( partido == null ) {
            return null;
        }

        String equipoLocal = null;
        String equipoVisitante = null;
        Long id = null;
        Integer jornada = null;
        Integer golesLocal = null;
        Integer golesVisitante = null;
        Boolean jugado = null;
        List<EventoPartidoDTO> eventos = null;

        equipoLocal = partidoEquipoLocalNombre( partido );
        equipoVisitante = partidoEquipoVisitanteNombre( partido );
        id = partido.getId();
        jornada = partido.getJornada();
        golesLocal = partido.getGolesLocal();
        golesVisitante = partido.getGolesVisitante();
        jugado = partido.getJugado();
        eventos = eventoPartidoListToEventoPartidoDTOList( partido.getEventos() );

        PartidoDTO partidoDTO = new PartidoDTO( id, jornada, equipoLocal, equipoVisitante, golesLocal, golesVisitante, jugado, eventos );

        return partidoDTO;
    }

    @Override
    public EventoPartidoDTO toEventoDTO(EventoPartido evento) {
        if ( evento == null ) {
            return null;
        }

        String nombreJugador = null;
        Long id = null;
        Integer minuto = null;
        EventoPartido.TipoEvento tipo = null;
        String descripcion = null;

        nombreJugador = eventoJugadorNombre( evento );
        id = evento.getId();
        minuto = evento.getMinuto();
        tipo = evento.getTipo();
        descripcion = evento.getDescripcion();

        EventoPartidoDTO eventoPartidoDTO = new EventoPartidoDTO( id, minuto, tipo, descripcion, nombreJugador );

        return eventoPartidoDTO;
    }

    private String partidoEquipoLocalNombre(Partido partido) {
        if ( partido == null ) {
            return null;
        }
        Equipo equipoLocal = partido.getEquipoLocal();
        if ( equipoLocal == null ) {
            return null;
        }
        String nombre = equipoLocal.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private String partidoEquipoVisitanteNombre(Partido partido) {
        if ( partido == null ) {
            return null;
        }
        Equipo equipoVisitante = partido.getEquipoVisitante();
        if ( equipoVisitante == null ) {
            return null;
        }
        String nombre = equipoVisitante.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    protected List<EventoPartidoDTO> eventoPartidoListToEventoPartidoDTOList(List<EventoPartido> list) {
        if ( list == null ) {
            return null;
        }

        List<EventoPartidoDTO> list1 = new ArrayList<EventoPartidoDTO>( list.size() );
        for ( EventoPartido eventoPartido : list ) {
            list1.add( toEventoDTO( eventoPartido ) );
        }

        return list1;
    }

    private String eventoJugadorNombre(EventoPartido eventoPartido) {
        if ( eventoPartido == null ) {
            return null;
        }
        Jugador jugador = eventoPartido.getJugador();
        if ( jugador == null ) {
            return null;
        }
        String nombre = jugador.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }
}
