package com.politecnics.football.engine.model;

public class EstadisticasJugador {
    private String nombreJugador;
    private int dorsal;
    private int goles;
    private int asistencias;
    private int tarjetasAmarillas;
    private int tarjetasRojas;
    private int partidosJugados;
    private int minutosJugados;
    private double ratingPromedio;
    private int valoracionesCount; // Para calcular promedio
    
    public EstadisticasJugador(String nombre, int dorsal) {
        this.nombreJugador = nombre;
        this.dorsal = dorsal;
    }
    
    public void registrarGol(int jornada) { this.goles++; }
    public void registrarAsistencia() { this.asistencias++; }
    public void registrarTarjetaAmarilla() { this.tarjetasAmarillas++; }
    public void registrarTarjetaRoja() { this.tarjetasRojas++; }
    public void registrarPartido(int minutos) {
        this.partidosJugados++;
        this.minutosJugados += minutos;
    }
    
    public void actualizarRating(double ratingPartido) {
        double totalRating = (this.ratingPromedio * this.valoracionesCount) + ratingPartido;
        this.valoracionesCount++;
        this.ratingPromedio = totalRating / this.valoracionesCount;
    }
    
    public double calcularRatingPartido() {
        // Logica simplificada para simulacion si no se pasa explicito
        return 6.0 + (Math.random() * 2.0); 
    }

    // Getters
    public int getGoles() { return goles; }
    public int getAsistencias() { return asistencias; }
    public int getTarjetasAmarillas() { return tarjetasAmarillas; }
    public int getTarjetasRojas() { return tarjetasRojas; }
    public int getPartidosJugados() { return partidosJugados; }
    public int getMinutosJugados() { return minutosJugados; }
    public double getRatingPromedio() { return ratingPromedio; }
}
