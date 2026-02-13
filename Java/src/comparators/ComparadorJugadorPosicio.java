package comparators;

import model.Jugador;
import java.util.Comparator;

/**
 * Comparador per ordenar jugadors per posició (alfabètic)
 * i qualitat (descendent).
 * 
 * S'utilitza per ordenar els jugadors d'un equip.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class ComparadorJugadorPosicio implements Comparator<Jugador> {
    
    /**
     * Compara dos jugadors segons els criteris:
     * 1. Posició (alfabèticament: DAV, DEF, MIG, POR)
     * 2. Qualitat (de major a menor) si la posició és igual
     * 
     * @param j1 El primer jugador
     * @param j2 El segon jugador
     * @return Negatiu si j1 < j2, positiu si j1 > j2, 0 si són iguals
     */
    @Override
    public int compare(Jugador j1, Jugador j2) {
        // Primer criteri: posició (alfabètic ascendent)
        int comparacioPosicio = j1.getPosicio().compareToIgnoreCase(j2.getPosicio());
        if (comparacioPosicio != 0) {
            return comparacioPosicio;
        }
        
        // Segon criteri: qualitat (descendent)
        return Double.compare(j2.getQualitat(), j1.getQualitat());
    }
}
