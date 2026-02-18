package com.politecnics.football.mapper;

import com.politecnics.football.dto.EntrenadorDTO;
import com.politecnics.football.dto.EquipoDTO;
import com.politecnics.football.dto.JugadorDTO;
import com.politecnics.football.dto.PresupuestoDTO;
import com.politecnics.football.dto.TacticaDTO;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Presupuesto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-18T13:10:59+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Eclipse Adoptium)"
)
@Component
public class EquipoMapperImpl implements EquipoMapper {

    @Autowired
    private JugadorMapper jugadorMapper;
    @Autowired
    private TacticaMapper tacticaMapper;
    @Autowired
    private EntrenadorMapper entrenadorMapper;

    @Override
    public EquipoDTO toDTO(Equipo equipo) {
        if ( equipo == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        Integer anyFundacion = null;
        String ciudad = null;
        String estadio = null;
        String presidente = null;
        Integer puntos = null;
        Integer golesFavor = null;
        Integer golesContra = null;
        Integer partidosJugados = null;
        Integer victorias = null;
        Integer empates = null;
        Integer derrotas = null;
        EntrenadorDTO entrenador = null;
        TacticaDTO tactica = null;
        List<JugadorDTO> jugadores = null;

        id = equipo.getId();
        nombre = equipo.getNombre();
        anyFundacion = equipo.getAnyFundacion();
        ciudad = equipo.getCiudad();
        estadio = equipo.getEstadio();
        presidente = equipo.getPresidente();
        puntos = equipo.getPuntos();
        golesFavor = equipo.getGolesFavor();
        golesContra = equipo.getGolesContra();
        partidosJugados = equipo.getPartidosJugados();
        victorias = equipo.getVictorias();
        empates = equipo.getEmpates();
        derrotas = equipo.getDerrotas();
        entrenador = entrenadorMapper.toDTO( equipo.getEntrenador() );
        tactica = tacticaMapper.toDTO( equipo.getTactica() );
        jugadores = jugadorListToJugadorDTOList( equipo.getJugadores() );

        EquipoDTO equipoDTO = new EquipoDTO( id, nombre, anyFundacion, ciudad, estadio, presidente, puntos, golesFavor, golesContra, partidosJugados, victorias, empates, derrotas, entrenador, tactica, jugadores );

        return equipoDTO;
    }

    @Override
    public Equipo toEntity(EquipoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Equipo.EquipoBuilder equipo = Equipo.builder();

        equipo.id( dto.id() );
        equipo.nombre( dto.nombre() );
        equipo.anyFundacion( dto.anyFundacion() );
        equipo.ciudad( dto.ciudad() );
        equipo.estadio( dto.estadio() );
        equipo.presidente( dto.presidente() );
        equipo.puntos( dto.puntos() );
        equipo.golesFavor( dto.golesFavor() );
        equipo.golesContra( dto.golesContra() );
        equipo.partidosJugados( dto.partidosJugados() );
        equipo.victorias( dto.victorias() );
        equipo.empates( dto.empates() );
        equipo.derrotas( dto.derrotas() );
        equipo.jugadores( jugadorDTOListToJugadorList( dto.jugadores() ) );
        equipo.tactica( tacticaMapper.toEntity( dto.tactica() ) );
        equipo.entrenador( entrenadorMapper.toEntity( dto.entrenador() ) );

        return equipo.build();
    }

    @Override
    public PresupuestoDTO toPresupuestoDTO(Presupuesto presupuesto) {
        if ( presupuesto == null ) {
            return null;
        }

        Double saldo = null;
        Double saldoInicial = null;
        Double ingresosPorPartido = null;
        Double gastosMantenimiento = null;

        saldo = presupuesto.getSaldo();
        saldoInicial = presupuesto.getSaldoInicial();
        ingresosPorPartido = presupuesto.getIngresosPorPartido();
        gastosMantenimiento = presupuesto.getGastosMantenimiento();

        PresupuestoDTO presupuestoDTO = new PresupuestoDTO( saldo, saldoInicial, ingresosPorPartido, gastosMantenimiento );

        return presupuestoDTO;
    }

    @Override
    public Presupuesto toPresupuestoEntity(PresupuestoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Presupuesto presupuesto = new Presupuesto();

        presupuesto.setSaldo( dto.saldo() );
        presupuesto.setSaldoInicial( dto.saldoInicial() );
        presupuesto.setIngresosPorPartido( dto.ingresosPorPartido() );
        presupuesto.setGastosMantenimiento( dto.gastosMantenimiento() );

        return presupuesto;
    }

    protected List<JugadorDTO> jugadorListToJugadorDTOList(List<Jugador> list) {
        if ( list == null ) {
            return null;
        }

        List<JugadorDTO> list1 = new ArrayList<JugadorDTO>( list.size() );
        for ( Jugador jugador : list ) {
            list1.add( jugadorMapper.toDTO( jugador ) );
        }

        return list1;
    }

    protected List<Jugador> jugadorDTOListToJugadorList(List<JugadorDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Jugador> list1 = new ArrayList<Jugador>( list.size() );
        for ( JugadorDTO jugadorDTO : list ) {
            list1.add( jugadorMapper.toEntity( jugadorDTO ) );
        }

        return list1;
    }
}
