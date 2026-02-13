package model;

import java.time.LocalDate;

/**
 * Classe abstracta que representa una persona del sistema (jugador o entrenador).
 * Conté les dades comunes a tots els membres de l'aplicació.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public abstract class Persona {
    private final String nom;
    private final String cognom;
    private final LocalDate dataNaixement;
    private double motivacio;
    private double souAnual;

    /**
     * Constructor de la classe Persona.
     * 
     * @param nom El nom de la persona (immutable)
     * @param cognom El cognom de la persona (immutable)
     * @param dataNaixement La data de naixement (immutable)
     * @param motivacio El nivell de motivació (1-10)
     * @param souAnual El sou anual en euros
     */
    public Persona(String nom, String cognom, LocalDate dataNaixement, double motivacio, double souAnual) {
        this.nom = nom;
        this.cognom = cognom;
        this.dataNaixement = dataNaixement;
        this.motivacio = motivacio;
        this.souAnual = souAnual;
    }

    /**
     * Mètode d'entrenament que augmenta la motivació en 0.2 punts.
     * Les classes filles poden sobreescriure aquest mètode.
     */
    public void entrenament() {
        this.motivacio += 0.2;
    }

    /**
     * Obté el nom de la persona.
     * 
     * @return El nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Obté el cognom de la persona.
     * 
     * @return El cognom
     */
    public String getCognom() {
        return cognom;
    }

    /**
     * Obté la data de naixement.
     * 
     * @return La data de naixement
     */
    public LocalDate getDataNaixement() {
        return dataNaixement;
    }

    /**
     * Obté el nivell de motivació actual.
     * 
     * @return La motivació (1-10)
     */
    public double getMotivacio() {
        return motivacio;
    }

    /**
     * Estableix un nou nivell de motivació.
     * 
     * @param motivacio El nou nivell de motivació
     */
    public void setMotivacio(double motivacio) {
        this.motivacio = motivacio;
    }

    /**
     * Obté el sou anual.
     * 
     * @return El sou anual en euros
     */
    public double getSouAnual() {
        return souAnual;
    }

    /**
     * Estableix un nou sou anual.
     * 
     * @param souAnual El nou sou anual
     */
    public void setSouAnual(double souAnual) {
        this.souAnual = souAnual;
    }

    /**
     * Retorna una representació en text de la persona.
     * 
     * @return String amb les dades bàsiques
     */
    @Override
    public String toString() {
        return String.format("%s %s (Naix: %s, Motivació: %.2f, Sou: %.2f€)",
                nom, cognom, dataNaixement, motivacio, souAnual);
    }
}
