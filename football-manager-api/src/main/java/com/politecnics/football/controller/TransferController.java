package com.politecnics.football.controller;

import com.politecnics.football.dto.PlayerDTO;
import com.politecnics.football.entity.Player;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.PlayerRepository;
import com.politecnics.football.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for player transfer operations: buy, sell, and market listing.
 */
@Slf4j
@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*")
public class TransferController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    /**
     * GET /api/transfers/market - Lists players available for transfer.
     * Returns all players not belonging to the specified team (or all if no teamId).
     */
    @GetMapping("/market")
    public ResponseEntity<List<PlayerDTO>> getMarketPlayers(@RequestParam(required = false) Long excludeTeamId) {
        List<Player> allPlayers = playerRepository.findAll();

        List<PlayerDTO> marketPlayers = allPlayers.stream()
                .filter(p -> excludeTeamId == null || p.getTeam() == null || !p.getTeam().getId().equals(excludeTeamId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(marketPlayers);
    }

    /**
     * POST /api/transfers/buy - Buy a player for a team.
     * Validates budget and transfers the player.
     */
    @PostMapping("/buy")
    @Transactional
    public ResponseEntity<String> buyPlayer(@RequestParam Long playerId, @RequestParam Long teamId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        Team buyingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Buying team not found with ID: " + teamId));

        Team sellingTeam = player.getTeam();

        if (sellingTeam != null && buyingTeam.getId().equals(sellingTeam.getId())) {
            throw new IllegalArgumentException("Player is already in this team");
        }

        long price = player.getMarketValue() != null ? player.getMarketValue() : 0;

        if (buyingTeam.getBudget() < price) {
            throw new IllegalArgumentException("Not enough budget. Required: " + price + ", Available: " + buyingTeam.getBudget());
        }

        // Execute transfer
        buyingTeam.setBudget(buyingTeam.getBudget() - price);
        if (sellingTeam != null) {
            sellingTeam.setBudget(sellingTeam.getBudget() + price);
            teamRepository.save(sellingTeam);
        }

        player.setTeam(buyingTeam);

        teamRepository.save(buyingTeam);
        playerRepository.save(player);

        log.info("Transfer: {} -> {} for €{}", player.getName(), buyingTeam.getName(), price);
        return ResponseEntity.ok("Transfer successful: " + player.getName() + " to " + buyingTeam.getName());
    }

    /**
     * POST /api/transfers/sell - Sell a player (makes them a free agent).
     */
    @PostMapping("/sell")
    @Transactional
    public ResponseEntity<String> sellPlayer(@RequestParam Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player not found with ID: " + playerId));

        Team sellingTeam = player.getTeam();
        if (sellingTeam == null) {
            throw new IllegalArgumentException("Player is already a free agent");
        }

        long price = player.getMarketValue() != null ? player.getMarketValue() : 0;
        sellingTeam.setBudget(sellingTeam.getBudget() + price);

        player.setTeam(null);

        teamRepository.save(sellingTeam);
        playerRepository.save(player);

        log.info("Player sold: {} from {} for €{}", player.getName(), sellingTeam.getName(), price);
        return ResponseEntity.ok("Player sold: " + player.getName() + " for €" + price);
    }

    private PlayerDTO convertToDTO(Player player) {
        return PlayerDTO.builder()
                .id(player.getId())
                .playerId(player.getPlayerId())
                .name(player.getName())
                .position(player.getPosition())
                .age(player.getAge())
                .overall(player.getOverall())
                .potential(player.getPotential())
                .goalsScored(player.getGoalsScored())
                .assists(player.getAssists())
                .yellowCards(player.getYellowCards())
                .redCards(player.getRedCards())
                .matchesPlayed(player.getMatchesPlayed())
                .marketValue(player.getMarketValue())
                .teamId(player.getTeam() != null ? player.getTeam().getTeamId() : null)
                .teamName(player.getTeam() != null ? player.getTeam().getName() : "Free Agent")
                .build();
    }
}
