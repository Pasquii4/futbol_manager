package com.politecnics.football.engine.model;

public class Lesion {
    private String tipoLesion;
    private int diasRecuperacion;
    
    public Lesion(String tipo, int dias) {
        this.tipoLesion = tipo;
        this.diasRecuperacion = dias;
    }
    
    public void avanzarRecuperacion() {
        if (diasRecuperacion > 0) diasRecuperacion--;
    }
    
    public boolean estaRecuperado() {
        return diasRecuperacion <= 0;
    }
    
    public String getTipoLesion() { return tipoLesion; }
    public int getDiasRecuperacion() { return diasRecuperacion; }
}
