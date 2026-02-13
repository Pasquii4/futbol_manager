package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe que gestiona els rankings individuals de la lliga.
 * Genera taules de màxims golejadors, assistents, millor rating, etc.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class RankingsLliga {
    private List<EstadisticasJugador> todasEstadisticas;
    private String temporada;
    
    /**
     * Constructor.
     * 
     * @param temporada Nom de la temporada
     */
    public RankingsLliga(String temporada) {
        this.temporada = temporada;
        this.todasEstadisticas = new ArrayList<>();
    }
    
    /**
     * Afegeix les estadístiques d'un jugador.
     * 
     * @param stats Estadístiques del jugador
     */
    public void afegirEstadisticas(EstadisticasJugador stats) {
        if (stats != null) {
            todasEstadisticas.add(stats);
        }
    }
    
    /**
     * Neteja totes les estadístiques.
     */
    public void netejarEstadisticas() {
        todasEstadisticas.clear();
    }
    
    /**
     * Obté el top N de golejadors.
     * 
     * @param n Nombre de jugadors a retornar
     * @return Llista ordenada dels millors golejadors
     */
    public List<EstadisticasJugador> getTopGoleadores(int n) {
        return todasEstadisticas.stream()
                                .sorted(Comparator.comparingInt(EstadisticasJugador::getGoles).reversed())
                                .limit(n)
                                .collect(Collectors.toList());
    }
    
    /**
     * Obté el top N d'assistents.
     * 
     * @param n Nombre de jugadors a retornar
     * @return Llista ordenada dels millors assistents
     */
    public List<EstadisticasJugador> getTopAsistentes(int n) {
        return todasEstadisticas.stream()
                                .sorted(Comparator.comparingInt(EstadisticasJugador::getAsistencias).reversed())
                                .limit(n)
                                .collect(Collectors.toList());
    }
    
    /**
     * Obté el top N de millors ratings.
     * Requereix mínim 5 partits jugats.
     * 
     * @param n Nombre de jugadors a retornar
     * @return Llista ordenada dels millors per rating
     */
    public List<EstadisticasJugador> getMejoresRating(int n) {
        return todasEstadisticas.stream()
                                .filter(stats -> stats.getPartidosJugados() >= 5)
                                .sorted(Comparator.comparingDouble(EstadisticasJugador::getRatingPromedio).reversed())
                                .limit(n)
                                .collect(Collectors.toList());
    }
    
    /**
     * Obté el millor porter (menys gols rebuts per partit).
     * 
     * @return Estadístiques del millor porter
     */
    public EstadisticasJugador getMejorPortero() {
        return todasEstadisticas.stream()
                                .filter(stats -> stats.getGolesRecibidos() > 0) // Ha jugat de porter
                                .filter(stats -> stats.getPartidosJugados() >= 3)
                                .min(Comparator.comparingDouble(EstadisticasJugador::getGolesRecibidosPorPartido))
                                .orElse(null);
    }
    
    /**
     * Obté el jugador més disciplinat (menys targetes).
     * 
     * @return Estadístiques del jugador més disciplinat
     */
    public EstadisticasJugador getJugadorMasDisciplinado() {
        return todasEstadisticas.stream()
                                .filter(stats -> stats.getPartidosJugados() >= 5)
                                .min(Comparator.comparingInt(stats -> 
                                    stats.getTarjetasAmarillas() + stats.getTarjetasRojas() * 2))
                                .orElse(null);
    }
    
    /**
     * Obté el jugador menys disciplinat (més targetes vermelles).
     * 
     * @return Estadístiques del jugador amb més targetes vermelles
     */
    public EstadisticasJugador getJugadorMenosIndisciplinado() {
        return todasEstadisticas.stream()
                                .max(Comparator.comparingInt(EstadisticasJugador::getTarjetasRojas))
                                .orElse(null);
    }
    
    /**
     * Genera totes les taules de rankings formatades.
     * 
     * @return String amb totes les taules
     */
    public String generarTablasRankings() {
        StringBuilder sb = new StringBuilder();
        
        // Top golejadors
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append("║           🏆 TOP 10 GOLEJADORS                 ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        
        List<EstadisticasJugador> topGoles = getTopGoleadores(10);
        for (int i = 0; i < topGoles.size(); i++) {
            EstadisticasJugador stats = topGoles.get(i);
            sb.append(String.format("║ %2d. %-20s (#%2d)  %3d gols      ║%n", 
                                  i + 1, 
                                  truncarNom(stats.getNombreJugador(), 20),
                                  stats.getDorsal(),
                                  stats.getGoles()));
        }
        sb.append("╚════════════════════════════════════════════════╝\n");
        
        // Top assistents
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append("║           🎯 TOP 10 ASSISTENTS                 ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        
        List<EstadisticasJugador> topAssists = getTopAsistentes(10);
        for (int i = 0; i < topAssists.size(); i++) {
            EstadisticasJugador stats = topAssists.get(i);
            sb.append(String.format("║ %2d. %-20s (#%2d)  %3d assists   ║%n", 
                                  i + 1,
                                  truncarNom(stats.getNombreJugador(), 20),
                                  stats.getDorsal(),
                                  stats.getAsistencias()));
        }
        sb.append("╚════════════════════════════════════════════════╝\n");
        
        // Millor rating
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append("║           ⭐ TOP 10 MILLOR RATING              ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        
        List<EstadisticasJugador> topRating = getMejoresRating(10);
        for (int i = 0; i < topRating.size(); i++) {
            EstadisticasJugador stats = topRating.get(i);
            sb.append(String.format("║ %2d. %-20s (#%2d)  %.2f rating   ║%n", 
                                  i + 1,
                                  truncarNom(stats.getNombreJugador(), 20),
                                  stats.getDorsal(),
                                  stats.getRatingPromedio()));
        }
        sb.append("╚════════════════════════════════════════════════╝\n");
        
        // Millor porter
        EstadisticasJugador millorPorter = getMejorPortero();
        if (millorPorter != null) {
            sb.append("\n╔════════════════════════════════════════════════╗\n");
            sb.append("║           🧤 MILLOR PORTER                     ║\n");
            sb.append("╠════════════════════════════════════════════════╣\n");
            sb.append(String.format("║  %s (#%d)%n", millorPorter.getNombreJugador(), millorPorter.getDorsal()));
            sb.append(String.format("║  Gols rebuts/partit: %.2f%n", millorPorter.getGolesRecibidosPorPartido()));
            sb.append(String.format("║  Parades: %d%n", millorPorter.getParadasPortero()));
            sb.append("╚════════════════════════════════════════════════╝\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Trunca un nom a la longitud màxima.
     */
    private String truncarNom(String nom, int maxLen) {
        if (nom == null) return "";
        if (nom.length() <= maxLen) {
            return String.format("%-" + maxLen + "s", nom);
        }
        return nom.substring(0, maxLen - 3) + "...";
    }
    
    // Getters
    
    public String getTemporada() {
        return temporada;
    }
    
    public List<EstadisticasJugador> getTodasEstadisticas() {
        return new ArrayList<>(todasEstadisticas);
    }
    
    public int getTotalJugadores() {
        return todasEstadisticas.size();
    }
}
