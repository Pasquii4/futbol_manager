package model;

/**
 * Classe que emmagatzema les dades de classificació d'un equip a la lliga.
 * Inclou punts, partits jugats, gols a favor i gols en contra.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class DadesClassificacio {
    private int punts;
    private int partitsJugats;
    private int golsAFavor;
    private int golsEnContra;

    /**
     * Constructor per defecte que inicialitza tots els valors a 0.
     */
    public DadesClassificacio() {
        this.punts = 0;
        this.partitsJugats = 0;
        this.golsAFavor = 0;
        this.golsEnContra = 0;
    }

    /**
     * Afegeix una victòria a les estadístiques.
     * Suma 3 punts i actualitza els gols a favor.
     * 
     * @param gols Els gols marcats en el partit guanyat
     * @param golsEnContra Els gols rebuts
     */
    public void afegirVictoria(int gols, int golsEnContra) {
        this.punts += 3;
        this.partitsJugats++;
        this.golsAFavor += gols;
        this.golsEnContra += golsEnContra;
    }

    /**
     * Afegeix un empat a les estadístiques.
     * Suma 1 punt i actualitza els gols.
     * 
     * @param gols Els gols marcats en el partit empatat
     */
    public void afegirEmpat(int gols) {
        this.punts += 1;
        this.partitsJugats++;
        this.golsAFavor += gols;
        this.golsEnContra += gols;
    }

    /**
     * Afegeix una derrota a les estadístiques.
     * No suma punts però actualitza gols i partits jugats.
     * 
     * @param gols Els gols marcats
     * @param golsEnContra Els gols rebuts
     */
    public void afegirDerrota(int gols, int golsEnContra) {
        this.partitsJugats++;
        this.golsAFavor += gols;
        this.golsEnContra += golsEnContra;
    }

    /**
     * Calcula la diferència de gols.
     * 
     * @return Gols a favor menys gols en contra
     */
    public int getDiferenciaGols() {
        return golsAFavor - golsEnContra;
    }

    /**
     * Obté els punts totals.
     * 
     * @return Els punts
     */
    public int getPunts() {
        return punts;
    }

    /**
     * Obté els partits jugats.
     * 
     * @return El nombre de partits jugats
     */
    public int getPartitsJugats() {
        return partitsJugats;
    }

    /**
     * Obté els gols a favor.
     * 
     * @return Els gols a favor
     */
    public int getGolsAFavor() {
        return golsAFavor;
    }

    /**
     * Obté els gols en contra.
     * 
     * @return Els gols en contra
     */
    public int getGolsEnContra() {
        return golsEnContra;
    }
}
