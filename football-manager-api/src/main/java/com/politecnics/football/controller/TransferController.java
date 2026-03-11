package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.repository.JugadorRepository;
import com.politecnics.football.repository.EquipoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for jugador transfer operations: buy, sell, and market listing.
 */
@Slf4j
@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*")
public class TransferController {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    /**
     * GET /api/transfers/market - Lists jugadores available for transfer.
     * Returns all jugadores not belonging to the specified team (or all if no teamId).
     */
    @GetMapping("/market")
    public ResponseEntity<List<PlayerDTO>> getMarketPlayers(@RequestParam(required = false) Long excludeTeamId) {
        List<Jugador> allPlayers = jugadorRepository.findAll();

        List<PlayerDTO> marketPlayers = allPlayers.stream()
                .filter(p -> excludeTeamId == null || p.getTeam() == null || !p.getTeam().getId().equals(excludeTeamId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(marketPlayers);
    }

    /**
     * POST /api/transfers/buy - Buy a jugador for a team.
     * Validates budget and transfers the jugador.
     */
    @PostMapping("/buy")
    @Transactional
    public ResponseEntity<String> buyPlayer(@RequestParam Long playerId, @RequestParam Long teamId) {
        Jugador jugador = jugadorRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Jugador not found with ID: " + playerId));

        Equipo buyingEquipo = equipoRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Buying team not found with ID: " + teamId));

        Equipo sellingEquipo = jugador.getTeam();

        if (sellingEquipo != null && buyingEquipo.getId().equals(sellingEquipo.getId())) {
            throw new IllegalArgumentException("Jugador is already in this team");
        }

        long price = jugador.getMarketValue() != null ? jugador.getMarketValue() : 0;

        if (buyingEquipo.getPresupuesto() != null && buyingEquipo.getBudget() < price) {
            throw new IllegalArgumentException("Not enough budget. Required: " + price + ", Available: " + buyingEquipo.getBudget());
        }

        // Execute transfer
        if (buyingEquipo.getPresupuesto() != null) {
            buyingEquipo.setBudget(buyingEquipo.getBudget() - price);
        }
        if (sellingEquipo != null && sellingEquipo.getPresupuesto() != null) {
            sellingEquipo.setBudget(sellingEquipo.getBudget() + price);
            equipoRepository.save(sellingEquipo);
        }

        jugador.setTeam(buyingEquipo);

        equipoRepository.save(buyingEquipo);
        jugadorRepository.save(jugador);

        log.info("Transfer: {} -> {} for €{}", jugador.getNombre(), buyingEquipo.getNombre(), price);
        return ResponseEntity.ok("Transfer successful: " + jugador.getNombre() + " to " + buyingEquipo.getNombre());
    }

    /**
     * POST /api/transfers/sell - Sell a jugador (makes them a free agent).
     */
    @PostMapping("/sell")
    @Transactional
    public ResponseEntity<String> sellPlayer(@RequestParam Long playerId) {
        Jugador jugador = jugadorRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Jugador not found with ID: " + playerId));

        Equipo sellingEquipo = jugador.getTeam();
        if (sellingEquipo == null) {
            throw new IllegalArgumentException("Jugador is already a free agent");
        }

        long price = jugador.getMarketValue() != null ? jugador.getMarketValue() : 0;
        
        if (sellingEquipo.getPresupuesto() != null) {
             sellingEquipo.setBudget(sellingEquipo.getBudget() + price);
             equipoRepository.save(sellingEquipo);
        }

        jugador.setTeam(null);
        jugadorRepository.save(jugador);

        log.info("Jugador sold: {} from {} for €{}", jugador.getNombre(), sellingEquipo.getNombre(), price);
        return ResponseEntity.ok("Jugador sold: " + jugador.getNombre() + " for €" + price);
    }

    private PlayerDTO convertToDTO(Jugador jugador) {
        return PlayerDTO.builder()
                .id(jugador.getId())
                .playerId(jugador.getJugadorId())
                .name(jugador.getNombre())
                .position(jugador.getPosicion() != null ? jugador.getPosicion().name() : null)
                .age((java.time.Period.between(jugador.getFechaNacimiento(), java.time.LocalDate.now()).getYears()))
                .overall(jugador.getCalidad() != null ? jugador.getCalidad().intValue() : 0)
                .potential(jugador.getCalidad() != null ? jugador.getCalidad().intValue() : 0)
                .goalsScored(jugador.getGoalsScored())
                .assists(jugador.getAssists())
                .yellowCards(jugador.getYellowCards())
                .redCards(jugador.getRedCards())
                .matchesPlayed(jugador.getMatchesPlayed())
                .marketValue(jugador.getMarketValue())
                .teamId(jugador.getTeam() != null ? jugador.getTeam().getTeamId() : null)
                .teamName(jugador.getTeam() != null ? jugador.getTeam().getNombre() : "Free Agent")
                .build();
    }
}
