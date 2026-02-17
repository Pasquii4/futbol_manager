package com.politecnics.football.mapper;

import com.politecnics.football.dto.TacticaDTO;
import com.politecnics.football.entity.Tactica;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TacticaMapper {
    TacticaDTO toDTO(Tactica tactica);
    Tactica toEntity(TacticaDTO dto);
}
