package com.politecnics.football.engine.model;

import java.time.LocalDate;

public class Entrenador extends Persona {
    private int torneosGanados;
    private boolean seleccionadorNacional;
    private double experiencia; // 0-100

    public Entrenador(String nom, String cognom, LocalDate dataNaixement, double motivacio, double souAnual) {
        super(nom, cognom, dataNaixement, motivacio, souAnual);
        this.experiencia = 50.0;
    }

    public double getExperiencia() { return experiencia; }
    public void setExperiencia(double experiencia) { this.experiencia = experiencia; }
}
