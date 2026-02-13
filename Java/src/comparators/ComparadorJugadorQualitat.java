package comparators;

import model.Jugador;
import java.util.Comparator;

/**
 * Comparador per ordenar jugadors per qualitat (descendent),
 * motivació (descendent) i cognom (alfabètic).
 * 
 * S'utilitza per ordenar els jugadors del mercat de fitxatges.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class ComparadorJugadorQualitat implements Comparator<Jugador> {
    
    /**
     * Compara dos jugadors segons els criteris:
     * 1. Qualitat (de major a menor)
     * 2. Motivació (de major a menor) si la qualitat és igual
     * 3. Cognom (alfabèticament) si qualitat i motivació són iguals
     * 
     * @param j1 El primer jugador
     * @param j2 El segon jugador
     * @return Negatiu si j1 < j2, positiu si j1 > j2, 0 si són iguals
     */
    @Override
    public int compare(Jugador j1, Jugador j2) {
        // Primer criteri: qualitat (descendent)
        int comparacioQualitat = Double.compare(j2.getQualitat(), j1.getQualitat());
        if (comparacioQualitat != 0) {
            return comparacioQualitat;
        }
        
        // Segon criteri: motivació (descendent)
        int comparacioMotivacio = Double.compare(j2.getMotivacio(), j1.getMotivacio());
        if (comparacioMotivacio != 0) {
            return comparacioMotivacio;
        }
        
        // Tercer criteri: cognom (alfabètic ascendent)
        return j1.getCognom().compareToIgnoreCase(j2.getCognom());
    }
}
