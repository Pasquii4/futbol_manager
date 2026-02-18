package com.politecnics.football.service;

import com.politecnics.football.entity.Match;
import com.politecnics.football.entity.Team;
import com.politecnics.football.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FixtureGenerator {

    @Autowired
    private MatchRepository matchRepository;

    @Transactional
    public void generateFixtures(List<Team> teams) {
        if (teams.size() < 2) return;
        
        // Ensure even number of teams
        if (teams.size() % 2 != 0) {
            // In a real app we might handle "bye" weeks, but for now assume even or drop one
            System.err.println("Odd number of teams, fixture generation might be uneven.");
        }

        int numTeams = teams.size();
        int numRounds = (numTeams - 1) * 2; // Double round robin
        int matchesPerRound = numTeams / 2;

        List<Match> allMatches = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2025, 8, 15); // Season start

        List<Team> rotatedTeams = new ArrayList<>(teams);
        // Remove first team, it stays fixed
        Team fixedTeam = rotatedTeams.remove(0);

        for (int round = 0; round < numRounds; round++) {
            int matchday = round + 1;
            LocalDate matchDate = startDate.plusWeeks(round);

            // Add fixed team match
            Team home, away;
            // Alternating home/away for the fixed team
            if (round % 2 == 0) {
                home = fixedTeam;
                away = rotatedTeams.get(rotatedTeams.size() - 1);
            } else {
                home = rotatedTeams.get(rotatedTeams.size() - 1);
                away = fixedTeam;
            }
            
            allMatches.add(Match.builder()
                    .matchday(matchday)
                    .homeTeam(home)
                    .awayTeam(away)
                    .played(false)
                    .matchDate(matchDate)
                    .homeGoals(0)
                    .awayGoals(0)
                    .build());

            // Pair other teams
            for (int i = 0; i < matchesPerRound - 1; i++) {
                int team1Idx = i;
                int team2Idx = rotatedTeams.size() - 2 - i;

                Team t1 = rotatedTeams.get(team1Idx);
                Team t2 = rotatedTeams.get(team2Idx);

                if (round % 2 == 0) {
                    home = t1;
                    away = t2;
                } else {
                    home = t2;
                    away = t1;
                }

                allMatches.add(Match.builder()
                        .matchday(matchday)
                        .homeTeam(home)
                        .awayTeam(away)
                        .played(false)
                        .matchDate(matchDate)
                        .homeGoals(0)
                        .awayGoals(0)
                        .build());
            }

            // Rotate teams
            Team last = rotatedTeams.remove(rotatedTeams.size() - 1);
            rotatedTeams.add(0, last);
        }
        
        matchRepository.saveAll(allMatches);
        System.out.println("Generated " + allMatches.size() + " matches for " + teams.size() + " teams.");
    }
}
