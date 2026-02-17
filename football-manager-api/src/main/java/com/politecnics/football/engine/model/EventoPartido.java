package com.politecnics.football.engine.model;

public class EventoPartido {
    public enum TipoEvento { GOL, ASISTENCIA, TARJETA_AMARILLA, TARJETA_ROJA, LESION, SUSTITUCION }
    
    private int minuto;
    private TipoEvento tipo;
    private Jugador jugador;
    private String descripcion;
    
    public EventoPartido(int minuto, TipoEvento tipo, Jugador jugador, String descripcion) {
        this.minuto = minuto;
        this.tipo = tipo;
        this.jugador = jugador;
        this.descripcion = descripcion;
    }
    
    public int getMinuto() { return minuto; }
    public TipoEvento getTipo() { return tipo; }
    public Jugador getJugador() { return jugador; }
    public String getDescripcion() { return descripcion; }
    
    @Override
    public String toString() {
        return String.format("%d' - %s (%s)", minuto, descripcion, tipo);
    }
}
