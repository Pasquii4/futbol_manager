package com.politecnics.football.engine.model;

import java.util.ArrayList;
import java.util.List;

public class Equip {
    private String nom;
    private List<Jugador> jugadors;
    private TacticaEquip tactica;
    private Entrenador entrenador;

    public Equip(String nom) {
        this.nom = nom;
        this.jugadors = new ArrayList<>();
        this.tactica = new TacticaEquip();
    }

    public void afegirJugador(Jugador jugador) {
        jugadors.add(jugador);
    }

    public void prepararPartido() {
        if (tactica != null) {
            tactica.generarAlineacionAutomatica(jugadors);
        }
    }

    // Getters & Setters
    public String getNom() { return nom; }
    public List<Jugador> getJugadors() { return jugadors; }
    public TacticaEquip getTactica() { return tactica; }
    public void setTactica(TacticaEquip tactica) { this.tactica = tactica; }
    public Entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenador entrenador) { this.entrenador = entrenador; }
}
