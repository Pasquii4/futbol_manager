package com.politecnics.football.mapper;

import com.politecnics.football.dto.EstadisticasJugadorDTO;
import com.politecnics.football.dto.JugadorDTO;
import com.politecnics.football.dto.LesionDTO;
import com.politecnics.football.entity.EstadisticasJugador;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Lesion;
import com.politecnics.football.entity.Posicion;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-18T11:04:22+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Eclipse Adoptium)"
)
@Component
public class JugadorMapperImpl implements JugadorMapper {

    @Override
    public JugadorDTO toDTO(Jugador jugador) {
        if ( jugador == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        String apellido = null;
        Integer dorsal = null;
        Posicion posicion = null;
        Double calidad = null;
        Double fatiga = null;
        Double forma = null;
        Double sueldo = null;
        Double motivacion = null;
        LesionDTO lesion = null;
        EstadisticasJugadorDTO estadisticas = null;

        id = jugador.getId();
        nombre = jugador.getNombre();
        apellido = jugador.getApellido();
        dorsal = jugador.getDorsal();
        posicion = jugador.getPosicion();
        calidad = jugador.getCalidad();
        fatiga = jugador.getFatiga();
        forma = jugador.getForma();
        sueldo = jugador.getSueldo();
        motivacion = jugador.getMotivacion();
        lesion = toLesionDTO( jugador.getLesion() );
        estadisticas = toEstadisticasDTO( jugador.getEstadisticas() );

        JugadorDTO jugadorDTO = new JugadorDTO( id, nombre, apellido, dorsal, posicion, calidad, fatiga, forma, sueldo, motivacion, lesion, estadisticas );

        return jugadorDTO;
    }

    @Override
    public Jugador toEntity(JugadorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Jugador.JugadorBuilder jugador = Jugador.builder();

        jugador.id( dto.id() );
        jugador.nombre( dto.nombre() );
        jugador.apellido( dto.apellido() );
        jugador.dorsal( dto.dorsal() );
        jugador.posicion( dto.posicion() );
        jugador.calidad( dto.calidad() );
        jugador.fatiga( dto.fatiga() );
        jugador.sueldo( dto.sueldo() );
        jugador.motivacion( dto.motivacion() );
        jugador.forma( dto.forma() );
        jugador.estadisticas( toEstadisticasEntity( dto.estadisticas() ) );
        jugador.lesion( toLesionEntity( dto.lesion() ) );

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
