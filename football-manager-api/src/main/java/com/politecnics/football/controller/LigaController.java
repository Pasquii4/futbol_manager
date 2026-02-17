package com.politecnics.football.controller;

import com.politecnics.football.dto.LigaDTO;
import com.politecnics.football.dto.PartidoDTO;
import com.politecnics.football.service.LigaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ligas")
@RequiredArgsConstructor
@Tag(name = "Ligas", description = "Operaciones sobre ligas")
public class LigaController {
    private final LigaService ligaService;

    @GetMapping
    @Operation(summary = "Obtener todas las ligas")
    public ResponseEntity<List<LigaDTO>> getAllLigas() {
        return ResponseEntity.ok(ligaService.getAllLigas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una lia")
    public ResponseEntity<LigaDTO> getLigaById(@PathVariable Long id) {
        return ResponseEntity.ok(ligaService.getLigaById(id));
    }

    @PostMapping("/{id}/simular")
    @Operation(summary = "Simular la jornada actual")
    public ResponseEntity<List<PartidoDTO>> simularJornada(@PathVariable Long id) {
        return ResponseEntity.ok(ligaService.simularJornada(id));
    }
    
    @GetMapping("/{id}/jornadas/{jornada}")
    @Operation(summary = "Obtener resultados de una jornada")
    public ResponseEntity<List<PartidoDTO>> getResultadosJornada(@PathVariable Long id, @PathVariable Integer jornada) {
        return ResponseEntity.ok(ligaService.getResultadosJornada(id, jornada));
    }
}
