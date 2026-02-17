package com.politecnics.football.mapper;

import com.politecnics.football.dto.LigaDTO;
import com.politecnics.football.entity.Liga;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EquipoMapper.class})
public interface LigaMapper {
    LigaDTO toDTO(Liga liga);
    Liga toEntity(LigaDTO dto);
}
