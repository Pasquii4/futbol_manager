package com.politecnics.football.service.simulation;

import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.MatchEvent;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Representa el estado volatil de un partido en curo durante la simulación minuto a minuto.
 */
@Data
public class MatchState {
    private int minute = 0;
    private int homeGoals = 0;
    private int awayGoals = 0;

    private double homeMomentum = 50.0;
    private double awayMomentum = 50.0;

    private double homePossession = 50.0;
    private double awayPossession = 50.0;

    private Equipo homeTeam;
    private Equipo awayTeam;

    private List<Jugador> homeStartingXI = new ArrayList<>();
    private List<Jugador> awayStartingXI = new ArrayList<>();
    
    private List<Jugador> homeBench = new ArrayList<>();
    private List<Jugador> awayBench = new ArrayList<>();

    // Real-time tracking of fatigue per player
    private Map<Long, Double> playerFatigue = new HashMap<>();

    // Keep track of events generated during this match
    private List<MatchEvent> events = new ArrayList<>();

    public MatchState(Equipo homeTeam, Equipo awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public void addEvent(MatchEvent event) {
        this.events.add(event);
    }
    
    public void incrementMinute() {
        this.minute++;
    }
}
