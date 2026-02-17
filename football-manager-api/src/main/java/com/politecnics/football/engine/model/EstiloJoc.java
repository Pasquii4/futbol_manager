package com.politecnics.football.engine.model;

public enum EstiloJoc {
    ULTRA_OFENSIVO(1.3, 0.6, "Ultra Ofensiu"),
    OFENSIVO(1.15, 0.85, "Ofensiu"),
    EQUILIBRADO(1.0, 1.0, "Equilibrat"),
    DEFENSIVO(0.85, 1.15, "Defensiu"),
    ULTRA_DEFENSIVO(0.6, 1.3, "Autobús");

    private final double multiplicadorAtaque;
    private final double multiplicadorDefensa;
    private final String nombre;

    EstiloJoc(double ataque, double defensa, String nombre) {
        this.multiplicadorAtaque = ataque;
        this.multiplicadorDefensa = defensa;
        this.nombre = nombre;
    }

    public double getMultiplicadorAtaque() { return multiplicadorAtaque; }
    public double getMultiplicadorDefensa() { return multiplicadorDefensa; }
    public String getNombre() { return nombre; }
}
