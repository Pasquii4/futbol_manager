package com.politecnics.football.controller;

import com.politecnics.football.entity.Player;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.PlayerRepository;
import com.politecnics.football.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@CrossOrigin(origins = "*")
public class TransferController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PostMapping("/buy")
    @Transactional
    public ResponseEntity<String> buyPlayer(@RequestParam Long playerId, @RequestParam Long teamId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        
        Team buyingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Buying team not found"));
        
        Team sellingTeam = player.getTeam();
        
        if (buyingTeam.getId().equals(sellingTeam.getId())) {
             return ResponseEntity.badRequest().body("Player is already in this team");
        }

        long price = player.getMarketValue() != null ? player.getMarketValue() : 0;
        
        if (buyingTeam.getBudget() < price) {
            return ResponseEntity.badRequest().body("Not enough budget");
        }

        // Transaction
        buyingTeam.setBudget(buyingTeam.getBudget() - price);
        sellingTeam.setBudget(sellingTeam.getBudget() + price);
        
        player.setTeam(buyingTeam);
        
        teamRepository.save(buyingTeam);
        teamRepository.save(sellingTeam);
        playerRepository.save(player);

        return ResponseEntity.ok("Transfer successful: " + player.getName() + " to " + buyingTeam.getName());
    }
}
