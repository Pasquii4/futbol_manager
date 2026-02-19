package com.politecnics.football.mapper;

import com.politecnics.football.dto.EntrenadorDTO;
import com.politecnics.football.entity.Entrenador;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-19T13:21:10+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Eclipse Adoptium)"
)
@Component
public class EntrenadorMapperImpl implements EntrenadorMapper {

    @Override
    public EntrenadorDTO toDTO(Entrenador entrenador) {
        if ( entrenador == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        String apellido = null;
        Integer torneosGanados = null;
        Boolean seleccionadorNacional = null;
        Double sueldoAnual = null;

        id = entrenador.getId();
        nombre = entrenador.getNombre();
        apellido = entrenador.getApellido();
        torneosGanados = entrenador.getTorneosGanados();
        seleccionadorNacional = entrenador.getSeleccionadorNacional();
        sueldoAnual = entrenador.getSueldoAnual();

        EntrenadorDTO entrenadorDTO = new EntrenadorDTO( id, nombre, apellido, torneosGanados, seleccionadorNacional, sueldoAnual );

        return entrenadorDTO;
    }

    @Override
    public Entrenador toEntity(EntrenadorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Entrenador.EntrenadorBuilder entrenador = Entrenador.builder();

        entrenador.id( dto.id() );
        entrenador.nombre( dto.nombre() );
        entrenador.apellido( dto.apellido() );
        entrenador.sueldoAnual( dto.sueldoAnual() );
        entrenador.torneosGanados( dto.torneosGanados() );
        entrenador.seleccionadorNacional( dto.seleccionadorNacional() );

        return entrenador.build();
    }
}
