package com.politecnics.football.mapper;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.dto.EstadisticasJugadorDTO;
import com.politecnics.football.dto.LesionDTO;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.EstadisticasJugador;
import com.politecnics.football.entity.Lesion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JugadorMapper {
    PlayerDTO toDTO(Jugador jugador);
    Jugador toEntity(PlayerDTO dto);
    
    EstadisticasJugadorDTO toEstadisticasDTO(EstadisticasJugador estadisticas);
    EstadisticasJugador toEstadisticasEntity(EstadisticasJugadorDTO dto);
    
    LesionDTO toLesionDTO(Lesion lesion);
    Lesion toLesionEntity(LesionDTO dto);
}
