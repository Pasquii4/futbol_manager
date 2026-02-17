package com.politecnics.football.mapper;

import com.politecnics.football.dto.EquipoDTO;
import com.politecnics.football.dto.PresupuestoDTO;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.Presupuesto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {JugadorMapper.class, TacticaMapper.class, EntrenadorMapper.class})
public interface EquipoMapper {
    EquipoDTO toDTO(Equipo equipo);
    Equipo toEntity(EquipoDTO dto);
    
    PresupuestoDTO toPresupuestoDTO(Presupuesto presupuesto);
    Presupuesto toPresupuestoEntity(PresupuestoDTO dto);
}
