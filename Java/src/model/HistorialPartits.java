package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe que gestiona l'historial de tots els partits jugats.
 * Permet consultar partits per equip, jornada o enfrontaments.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class HistorialPartits {
    private List<Partit> partitsJugats;
    private Map<String, List<Partit>> partitsPorEquip;
    
    /**
     * Constructor.
     */
    public HistorialPartits() {
        this.partitsJugats = new ArrayList<>();
        this.partitsPorEquip = new HashMap<>();
    }
    
    /**
     * Afegeix un partit a l'historial.
     * 
     * @param partit El partit a afegir
     */
    public void afegirPartit(Partit partit) {
        if (partit == null) {
            return;
        }
        
        partitsJugats.add(partit);
        
        // Actualitzar mapa per equips
        String nomEquip1 = partit.getEquip1().getNom();
        String nomEquip2 = partit.getEquip2().getNom();
        
        partitsPorEquip.computeIfAbsent(nomEquip1, k -> new ArrayList<>()).add(partit);
        partitsPorEquip.computeIfAbsent(nomEquip2, k -> new ArrayList<>()).add(partit);
    }
    
    /**
     * Obté tots els partits d'un equip.
     * 
     * @param nombreEquip Nom de l'equip
     * @return Llista de partits de l'equip
     */
    public List<Partit> getPartitsEquip(String nombreEquip) {
        return partitsPorEquip.getOrDefault(nombreEquip, new ArrayList<>());
    }
    
    /**
     * Cerca l'últim enfrontament entre dos equips.
     * 
     * @param eq1 Primer equip
     * @param eq2 Segon equip
     * @return L'últim partit entre els dos equips, o null si no n'hi ha
     */
    public Partit getUltimoEnfrentamiento(Equip eq1, Equip eq2) {
        for (int i = partitsJugats.size() - 1; i >= 0; i--) {
            Partit p = partitsJugats.get(i);
            if ((p.getEquip1().equals(eq1) && p.getEquip2().equals(eq2)) ||
                (p.getEquip1().equals(eq2) && p.getEquip2().equals(eq1))) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * Obté tots els partits d'una jornada específica.
     * 
     * @param jornada Número de jornada
     * @return Llista de partits de la jornada
     */
    public List<Partit> getPartitsJornada(int jornada) {
        return partitsJugats.stream()
                           .filter(p -> p.getJornada() == jornada)
                           .collect(Collectors.toList());
    }
    
    /**
     * Genera un resum de la temporada amb estadístiques globals.
     * 
     * @return String amb el resum
     */
    public String generarResumenTemporada() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════════╗\n");
        sb.append("║        📊 RESUM DE LA TEMPORADA                ║\n");
        sb.append("╠════════════════════════════════════════════════╣\n");
        sb.append(String.format("║  Total partits jugats:  %d%n", partitsJugats.size()));
        
        if (!partitsJugats.isEmpty()) {
            int totalGols = partitsJugats.stream()
                                        .mapToInt(p -> p.getGolsEquip1() + p.getGolsEquip2())
                                        .sum();
            double mitjanaGols = (double) totalGols / partitsJugats.size();
            
            sb.append(String.format("║  Total gols:            %d%n", totalGols));
            sb.append(String.format("║  Mitjana gols/partit:   %.2f%n", mitjanaGols));
        }
        
        sb.append(String.format("║  Equips participant:    %d%n", partitsPorEquip.size()));
        sb.append("╚════════════════════════════════════════════════╝\n");
        
        return sb.toString();
    }
    
    /**
     * Neteja tot l'historial.
     */
    public void netejarHistorial() {
        partitsJugats.clear();
        partitsPorEquip.clear();
    }
    
    // Getters
    
    public List<Partit> getPartitsJugats() {
        return new ArrayList<>(partitsJugats);
    }
    
    public int getTotalPartits() {
        return partitsJugats.size();
    }
}
