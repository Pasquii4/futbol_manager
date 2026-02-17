package com.politecnics.football.controller;

import com.politecnics.football.dto.EquipoDTO;
import com.politecnics.football.service.EquipoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
@Tag(name = "Equipos", description = "Operaciones sobre equipos")
public class EquipoController {
    private final EquipoService equipoService;

    @GetMapping
    @Operation(summary = "Obtener todos los equipos")
    public ResponseEntity<List<EquipoDTO>> getAllEquipos() {
        return ResponseEntity.ok(equipoService.getAllEquipos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un equipo")
    public ResponseEntity<EquipoDTO> getEquipoById(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.getEquipoById(id));
    }
    
    @GetMapping("/liga/{ligaId}")
    @Operation(summary = "Obtener equipos de una liga")
    public ResponseEntity<List<EquipoDTO>> getEquiposByLiga(@PathVariable Long ligaId) {
        return ResponseEntity.ok(equipoService.getEquiposByLiga(ligaId));
    }
}
