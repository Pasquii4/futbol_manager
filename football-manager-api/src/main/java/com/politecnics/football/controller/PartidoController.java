package com.politecnics.football.controller;

import com.politecnics.football.dto.PartidoDTO;
import com.politecnics.football.service.PartidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partidos")
@RequiredArgsConstructor
@Tag(name = "Partidos", description = "Operaciones sobre partidos")
public class PartidoController {
    private final PartidoService partidoService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un partido")
    public ResponseEntity<PartidoDTO> getPartidoById(@PathVariable Long id) {
        return ResponseEntity.ok(partidoService.getPartidoById(id));
    }
}
