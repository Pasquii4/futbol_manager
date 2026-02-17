package com.politecnics.football.engine.model;

public enum Formacion {
    F_4_4_2(4, 4, 2, 1.0, 1.0, "4-4-2 Clàssica"),
    F_4_3_3(4, 3, 3, 1.2, 0.8, "4-3-3 Ofensiva"),
    F_3_5_2(3, 5, 2, 1.1, 0.9, "3-5-2 Possessió"),
    F_4_5_1(4, 5, 1, 0.8, 1.2, "4-5-1 Defensiva"),
    F_5_3_2(5, 3, 2, 0.7, 1.3, "5-3-2 Muralla"),
    F_3_4_3(3, 4, 3, 1.3, 0.7, "3-4-3 Total");

    private final int defensas;
    private final int mediocampistas;
    private final int delanteros;
    private final double bonusAtaque;
    private final double bonusDefensa;
    private final String nombre;

    Formacion(int defensas, int mediocampistas, int delanteros, double bonusAtaque, double bonusDefensa, String nombre) {
        this.defensas = defensas;
        this.mediocampistas = mediocampistas;
        this.delanteros = delanteros;
        this.bonusAtaque = bonusAtaque;
        this.bonusDefensa = bonusDefensa;
        this.nombre = nombre;
    }

    public int getDefensas() { return defensas; }
    public int getMediocampistas() { return mediocampistas; }
    public int getDelanteros() { return delanteros; }
    public double getBonusAtaque() { return bonusAtaque; }
    public double getBonusDefensa() { return bonusDefensa; }
    public String getNombre() { return nombre; }
}
