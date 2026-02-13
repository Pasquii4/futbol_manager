package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe que gestiona les estadístiques d'un jugador.
 * Registra gols, assistències, targetes, minuts jugats i rating.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
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
    
    // Estadístiques de porter
    private int paradasPortero;
    private int golesRecibidos;
    
    // Mapa de gols per jornada
    private Map<Integer, Integer> golesPorJornada;
    
    /**
     * Constructor.
     * 
     * @param nombreJugador Nom del jugador
     * @param dorsal Dorsal del jugador
     */
    public EstadisticasJugador(String nombreJugador, int dorsal) {
        this.nombreJugador = nombreJugador;
        this.dorsal = dorsal;
        this.goles = 0;
        this.asistencias = 0;
        this.tarjetasAmarillas = 0;
        this.tarjetasRojas = 0;
        this.partidosJugados = 0;
        this.minutosJugados = 0;
        this.ratingPromedio = 6.0;
        this.paradasPortero = 0;
        this.golesRecibidos = 0;
        this.golesPorJornada = new HashMap<>();
    }
    
    /**
     * Registra un gol marcat en una jornada específica.
     * 
     * @param jornada Número de jornada
     */
    public void registrarGol(int jornada) {
        this.goles++;
        golesPorJornada.put(jornada, golesPorJornada.getOrDefault(jornada, 0) + 1);
    }
    
    /**
     * Registra una assistència.
     */
    public void registrarAsistencia() {
        this.asistencias++;
    }
    
    /**
     * Registra una targeta groga.
     */
    public void registrarTarjetaAmarilla() {
        this.tarjetasAmarillas++;
    }
    
    /**
     * Registra una targeta vermella.
     */
    public void registrarTarjetaRoja() {
        this.tarjetasRojas++;
    }
    
    /**
     * Registra un partit jugat.
     * 
     * @param minutos Minuts jugats en aquest partit
     */
    public void registrarPartido(int minutos) {
        this.partidosJugados++;
        this.minutosJugados += minutos;
    }
    
    /**
     * Registra una parada del porter.
     */
    public void registrarParada() {
        this.paradasPortero++;
    }
    
    /**
     * Registra un gol rebut pel porter.
     */
    public void registrarGolRecibido() {
        this.golesRecibidos++;
    }
    
    /**
     * Actualitza el rating amb el rating d'un partit nou.
     * Calcula la mitjana ponderada.
     * 
     * @param ratingPartido Rating del partit actual
     */
    public void actualizarRating(double ratingPartido) {
        if (partidosJugados == 0) {
            this.ratingPromedio = ratingPartido;
        } else {
            // Mitjana ponderada
            this.ratingPromedio = ((ratingPromedio * (partidosJugados - 1)) + ratingPartido) / partidosJugados;
        }
    }
    
    /**
     * Calcula el rating del partit basat en les estadístiques.
     * Fórmula: (gols × 1.5 + assistències × 1.0 - targetes grogues × 0.2 - targetes vermelles × 0.5) / partits + 6.0
     * 
     * @return Rating entre 0 i 10
     */
    public double calcularRatingPartido() {
        if (partidosJugados == 0) {
            return 6.0;
        }
        
        double rating = 6.0;
        rating += (goles * 1.5) / (double) partidosJugados;
        rating += (asistencias * 1.0) / (double) partidosJugados;
        rating -= (tarjetasAmarillas * 0.2) / (double) partidosJugados;
        rating -= (tarjetasRojas * 0.5) / (double) partidosJugados;
        
        // Limitar entre 0 i 10
        return Math.max(0.0, Math.min(10.0, rating));
    }
    
    /**
     * Calcula la mitjana de gols rebuts per partit (porters).
     * 
     * @return Gols rebuts per partit
     */
    public double getGolesRecibidosPorPartido() {
        if (partidosJugados == 0) {
            return 0.0;
        }
        return (double) golesRecibidos / partidosJugados;
    }
    
    /**
     * Genera un informe formatat amb totes les estadístiques.
     * 
     * @return String amb l'informe
     */
    public String generarReporte() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  📊 ESTADÍSTIQUES: %s (#%d)%n", nombreJugador, dorsal));
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Partits jugats:        %d%n", partidosJugados));
        sb.append(String.format("║  Minuts jugats:         %d%n", minutosJugados));
        sb.append(String.format("║  Rating mitjà:          %.2f%n", ratingPromedio));
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  ⚽ Gols:               %d%n", goles));
        sb.append(String.format("║  🎯 Assistències:       %d%n", asistencias));
        sb.append(String.format("║  🟨 Targetes grogues:  %d%n", tarjetasAmarillas));
        sb.append(String.format("║  🟥 Targetes vermelles:%d%n", tarjetasRojas));
        
        // Estadístiques de porter
        if (paradasPortero > 0 || golesRecibidos > 0) {
            sb.append("╠════════════════════════════════════════════════╣\n");
            sb.append("║  ESTADÍSTIQUES DE PORTER:\n");
            sb.append(String.format("║  🧤 Parades:            %d%n", paradasPortero));
            sb.append(String.format("║  🥅 Gols rebuts:        %d%n", golesRecibidos));
            sb.append(String.format("║  📉 Gols/partit:        %.2f%n", getGolesRecibidosPorPartido()));
        }
        
        sb.append("╚════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    // Getters
    
    public String getNombreJugador() {
        return nombreJugador;
    }
    
    public int getDorsal() {
        return dorsal;
    }
    
    public int getGoles() {
        return goles;
    }
    
    public int getAsistencias() {
        return asistencias;
    }
    
    public int getTarjetasAmarillas() {
        return tarjetasAmarillas;
    }
    
    public int getTarjetasRojas() {
        return tarjetasRojas;
    }
    
    public int getPartidosJugados() {
        return partidosJugados;
    }
    
    public int getMinutosJugados() {
        return minutosJugados;
    }
    
    public double getRatingPromedio() {
        return ratingPromedio;
    }
    
    public int getParadasPortero() {
        return paradasPortero;
    }
    
    public int getGolesRecibidos() {
        return golesRecibidos;
    }
    
    public Map<Integer, Integer> getGolesPorJornada() {
        return golesPorJornada;
    }
    
    @Override
    public String toString() {
        return String.format("%s (#%d) - %d gols, %d assists, Rating: %.2f", 
                           nombreJugador, dorsal, goles, asistencias, ratingPromedio);
    }
}
