package com.politecnics.football.mapper;

import com.politecnics.football.dto.PartidoDTO;
import com.politecnics.football.dto.EventoPartidoDTO;
import com.politecnics.football.entity.Partido;
import com.politecnics.football.entity.EventoPartido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartidoMapper {
    
    @Mapping(source = "equipoLocal.nombre", target = "equipoLocal")
    @Mapping(source = "equipoVisitante.nombre", target = "equipoVisitante")
    PartidoDTO toDTO(Partido partido);
    
    @Mapping(source = "jugador.nombre", target = "nombreJugador")
    EventoPartidoDTO toEventoDTO(EventoPartido evento);
}
