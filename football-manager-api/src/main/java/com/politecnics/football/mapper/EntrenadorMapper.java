package com.politecnics.football.mapper;

import com.politecnics.football.dto.EntrenadorDTO;
import com.politecnics.football.entity.Entrenador;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntrenadorMapper {
    EntrenadorDTO toDTO(Entrenador entrenador);
    Entrenador toEntity(EntrenadorDTO dto);
}
