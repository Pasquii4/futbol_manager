package model;

import java.time.LocalDate;

/**
 * Classe que representa un entrenador de futbol.
 * Hereta de Persona i afegeix atributs específics com tornejos guanyats i si ha estat seleccionador.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class Entrenador extends Persona {
    private int tornejosGuanyats;
    private boolean seleccionadorNacional;

    /**
     * Constructor complet d'Entrenador.
     * 
     * @param nom El nom de l'entrenador
     * @param cognom El cognom de l'entrenador
     * @param dataNaixement La data de naixement
     * @param motivacio El nivell de motivació inicial
     * @param souAnual El sou anual
     * @param tornejosGuanyats El nombre de tornejos guanyats
     * @param seleccionadorNacional Si ha estat seleccionador nacional
     */
    public Entrenador(String nom, String cognom, LocalDate dataNaixement, double motivacio,
                      double souAnual, int tornejosGuanyats, boolean seleccionadorNacional) {
        super(nom, cognom, dataNaixement, motivacio, souAnual);
        this.tornejosGuanyats = tornejosGuanyats;
        this.seleccionadorNacional = seleccionadorNacional;
    }

    /**
     * Mètode d'entrenament sobreescrit.
     * Si és seleccionador nacional: augmenta motivació en 0.3
     * Si no és seleccionador: augmenta motivació en 0.15
     */
    @Override
    public void entrenament() {
        double increment = seleccionadorNacional ? 0.3 : 0.15;
        setMotivacio(getMotivacio() + increment);
        System.out.println(String.format("  👔 %s %s: Motivació +%.2f (ara: %.2f)", 
                getNom(), getCognom(), increment, getMotivacio()));
    }

    /**
     * Incrementa el sou de l'entrenador en un 0.5%.
     */
    public void incrementarSou() {
        double nouSou = getSouAnual() * 1.005;
        setSouAnual(nouSou);
    }

    /**
     * Obté el nombre de tornejos guanyats.
     * 
     * @return Els tornejos guanyats
     */
    public int getTornejosGuanyats() {
        return tornejosGuanyats;
    }

    /**
     * Comprova si ha estat seleccionador nacional.
     * 
     * @return true si ha estat seleccionador, false altrament
     */
    public boolean isSeleccionadorNacional() {
        return seleccionadorNacional;
    }

    /**
     * Retorna una representació detallada de l'entrenador.
     * 
     * @return String amb totes les dades de l'entrenador
     */
    @Override
    public String toString() {
        return String.format("👔 Entrenador: %s %s | Tornejos: %d | Seleccionador: %s | Motivació: %.2f | Sou: %.2f€",
                getNom(), getCognom(), tornejosGuanyats, 
                seleccionadorNacional ? "Sí" : "No", getMotivacio(), getSouAnual());
    }
}
