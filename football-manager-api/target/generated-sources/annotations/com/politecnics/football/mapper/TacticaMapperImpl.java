package com.politecnics.football.mapper;

import com.politecnics.football.dto.TacticaDTO;
import com.politecnics.football.entity.EstiloJuego;
import com.politecnics.football.entity.Formacion;
import com.politecnics.football.entity.Tactica;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-18T13:10:59+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.10 (Eclipse Adoptium)"
)
@Component
public class TacticaMapperImpl implements TacticaMapper {

    @Override
    public TacticaDTO toDTO(Tactica tactica) {
        if ( tactica == null ) {
            return null;
        }

        Long id = null;
        Formacion formacion = null;
        EstiloJuego estiloJuego = null;
        Integer intensidadPresion = null;

        id = tactica.getId();
        formacion = tactica.getFormacion();
        estiloJuego = tactica.getEstiloJuego();
        intensidadPresion = tactica.getIntensidadPresion();

        TacticaDTO tacticaDTO = new TacticaDTO( id, formacion, estiloJuego, intensidadPresion );

        return tacticaDTO;
    }

    @Override
    public Tactica toEntity(TacticaDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Tactica.TacticaBuilder tactica = Tactica.builder();

        tactica.id( dto.id() );
        tactica.formacion( dto.formacion() );
        tactica.estiloJuego( dto.estiloJuego() );
        tactica.intensidadPresion( dto.intensidadPresion() );

        return tactica.build();
    }
}
