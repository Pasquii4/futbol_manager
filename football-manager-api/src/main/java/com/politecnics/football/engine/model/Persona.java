package com.politecnics.football.engine.model;

import java.time.LocalDate;

public abstract class Persona {
    private String nom;
    private String cognom;
    private LocalDate dataNaixement;
    private double motivacio; // 0-10
    private double souAnual;
    
    public Persona(String nom, String cognom, LocalDate dataNaixement, double motivacio, double souAnual) {
        this.nom = nom;
        this.cognom = cognom;
        this.dataNaixement = dataNaixement;
        this.motivacio = Math.max(0, Math.min(10, motivacio));
        this.souAnual = souAnual;
    }
    
    public void entrenament() {
        this.motivacio = Math.min(10, this.motivacio + 0.2);
    }
    
    // Getters and Setters
    public String getNom() { return nom; }
    public String getCognom() { return cognom; }
    public LocalDate getDataNaixement() { return dataNaixement; }
    public double getMotivacio() { return motivacio; }
    public void setMotivacio(double motivacio) { this.motivacio = motivacio; }
    public double getSouAnual() { return souAnual; }
    
    @Override
    public String toString() {
        return nom + " " + cognom;
    }
}
