package com.politecnics.football.mapper;

import com.politecnics.football.dto.EquipoDTO;
import com.politecnics.football.dto.LigaDTO;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.Liga;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-18T11:04:22+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Eclipse Adoptium)"
)
@Component
public class LigaMapperImpl implements LigaMapper {

    @Autowired
    private EquipoMapper equipoMapper;

    @Override
    public LigaDTO toDTO(Liga liga) {
        if ( liga == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        Integer jornadaActual = null;
        Integer totalJornadas = null;
        Boolean finalizada = null;
        List<EquipoDTO> equipos = null;

        id = liga.getId();
        nombre = liga.getNombre();
        jornadaActual = liga.getJornadaActual();
        totalJornadas = liga.getTotalJornadas();
        finalizada = liga.getFinalizada();
        equipos = equipoListToEquipoDTOList( liga.getEquipos() );

        LigaDTO ligaDTO = new LigaDTO( id, nombre, jornadaActual, totalJornadas, finalizada, equipos );

        return ligaDTO;
    }

    @Override
    public Liga toEntity(LigaDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Liga.LigaBuilder liga = Liga.builder();

        liga.id( dto.id() );
        liga.nombre( dto.nombre() );
        liga.jornadaActual( dto.jornadaActual() );
        liga.totalJornadas( dto.totalJornadas() );
        liga.finalizada( dto.finalizada() );
        liga.equipos( equipoDTOListToEquipoList( dto.equipos() ) );

        return liga.build();
    }

    protected List<EquipoDTO> equipoListToEquipoDTOList(List<Equipo> list) {
        if ( list == null ) {
            return null;
        }

        List<EquipoDTO> list1 = new ArrayList<EquipoDTO>( list.size() );
        for ( Equipo equipo : list ) {
            list1.add( equipoMapper.toDTO( equipo ) );
        }

        return list1;
    }

    protected List<Equipo> equipoDTOListToEquipoList(List<EquipoDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Equipo> list1 = new ArrayList<Equipo>( list.size() );
        for ( EquipoDTO equipoDTO : list ) {
            list1.add( equipoMapper.toEntity( equipoDTO ) );
        }

        return list1;
    }
}
