package com.politecnics.football.engine.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;

public class Jugador extends Persona {
    public static final String[] POSICIONS = {"POR", "DEF", "MIG", "DAV"};
    
    private int dorsal;
    private String posicio;
    private double qualitat;
    private double forma;
    private double fatiga;
    private Lesion lesionActual;
    private EstadisticasJugador estadisticas;
    
    private static final Random random = new Random();

    public Jugador(String nom, String cognom, LocalDate dataNaixement, double motivacio, 
                   double souAnual, int dorsal, String posicio, double qualitat) {
        super(nom, cognom, dataNaixement, motivacio, souAnual);
        this.dorsal = dorsal;
        this.posicio = posicio;
        this.qualitat = qualitat;
        this.forma = 7.0;
        this.fatiga = 0.0;
        this.estadisticas = new EstadisticasJugador(nom, dorsal);
    }
    
    public double getQualitatEfectiva() {
        if (!estaDisponible()) return 0.0;
        double factorForma = 0.8 + (this.forma / 50.0);
        double factorFatiga = 1.0 - (this.fatiga / 100.0);
        return this.qualitat * factorForma * factorFatiga;
    }
    
    public boolean estaDisponible() {
        return lesionActual == null || lesionActual.estaRecuperado();
    }
    
    public void aumentarFatiga(double incremento) {
        this.fatiga = Math.min(100.0, this.fatiga + incremento);
    }
    
    public void registrarGol() { estadisticas.registrarGol(0); actualizarForma(0.3); }
    public void registrarAssistencia() { estadisticas.registrarAsistencia(); actualizarForma(0.2); }
    public void registrarPartit() { estadisticas.registrarPartido(90); }
    public void registrarTargetaGroga() { estadisticas.registrarTarjetaAmarilla(); actualizarForma(-0.1); }
    public void registrarTargetaVermella() { estadisticas.registrarTarjetaRoja(); actualizarForma(-0.5); }
    
    public void actualizarForma(double canvi) {
        this.forma = Math.max(0.0, Math.min(10.0, this.forma + canvi));
    }
    
    public void actualitzarFormaDespresPartit(boolean victoria, boolean empat) {
        if (victoria) actualizarForma(0.2);
        else if (!empat) actualizarForma(-0.2);
    }

    // Getters and Setters simplified
    public int getDorsal() { return dorsal; }
    public String getPosicio() { return posicio; }
    public double getQualitat() { return qualitat; }
    
    public double getFatiga() { return fatiga; }
    public void setFatiga(double fatiga) { this.fatiga = fatiga; }
    
    public double getForma() { return forma; }
    public void setForma(double forma) { this.forma = forma; }

    public Lesion getLesionActual() { return lesionActual; }
    public void setLesionActual(Lesion lesion) { this.lesionActual = lesion; }
    
    public EstadisticasJugador getEstadisticas() { return estadisticas; }
    public void setEstadisticas(EstadisticasJugador estadisticas) { this.estadisticas = estadisticas; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return dorsal == jugador.dorsal && getNom().equals(jugador.getNom());
    }

    public double getPesoGol() {
        switch (posicio) {
            case "DAV": return 10.0;
            case "MIG": return 3.0;
            case "DEF": return 1.0;
            case "POR": return 0.1;
            default: return 1.0;
        }
    }

    public double getPesoTarjetas() {
        switch (posicio) {
            case "DEF": return 2.0; // Higher chance for defenders
            case "MIG": return 1.5;
            case "POR": return 0.2;
            case "DAV": return 0.5;
            default: return 1.0;
        }
    }
}
