package model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;

/**
 * Classe que representa un jugador de futbol.
 * Hereta de Persona i afegeix atributs específics com dorsal, posició i qualitat.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class Jugador extends Persona {
    private static int totalJugadors = 0;
    public static final String[] POSICIONS = {"POR", "DEF", "MIG", "DAV"};
    
    private int dorsal;
    private String posicio;
    private double qualitat;
    
    // Estadístiques del jugador
    private int golsMarcats;
    private int assistencies;
    private int partitsJugats;
    private int tarjetesGrogues;
    private int tarjetesVermelles;
    private double forma; // Forma actual del jugador (0-10)
    
    private static final Random random = new Random();

    /**
     * Constructor complet de Jugador.
     * 
     * @param nom El nom del jugador
     * @param cognom El cognom del jugador
     * @param dataNaixement La data de naixement
     * @param motivacio El nivell de motivació inicial
     * @param souAnual El sou anual
     * @param dorsal El número de dorsal
     * @param posicio La posició (POR, DEF, MIG, DAV)
     * @param qualitat La qualitat del jugador (30-100)
     */
    public Jugador(String nom, String cognom, LocalDate dataNaixement, double motivacio, 
                   double souAnual, int dorsal, String posicio, double qualitat) {
        super(nom, cognom, dataNaixement, motivacio, souAnual);
        this.dorsal = dorsal;
        this.posicio = posicio;
        this.qualitat = qualitat;
        
        // Inicialitzar estadístiques
        this.golsMarcats = 0;
        this.assistencies = 0;
        this.partitsJugats = 0;
        this.tarjetesGrogues = 0;
        this.tarjetesVermelles = 0;
        this.forma = 7.0; // Forma inicial neutral
        
        totalJugadors++;
    }

    /**
     * Mètode d'entrenament que augmenta la motivació (de la classe pare)
     * i la qualitat del jugador de manera aleatòria.
     * - 70% probabilitat d'augmentar 0.1
     * - 20% probabilitat d'augmentar 0.2
     * - 10% probabilitat d'augmentar 0.3
     */
    @Override
    public void entrenament() {
        super.entrenament(); // Augmenta motivació 0.2
        
        double increment;
        int prob = random.nextInt(100);
        
        if (prob < 70) {
            increment = 0.1;
        } else if (prob < 90) {
            increment = 0.2;
        } else {
            increment = 0.3;
        }
        
        this.qualitat += increment;
        System.out.println(String.format("  💪 %s %s: Qualitat +%.1f (ara: %.2f)", 
                getNom(), getCognom(), increment, this.qualitat));
    }

    /**
     * Mètode que simula un possible canvi de posició del jugador.
     * Té un 5% de possibilitats de canviar de posició.
     * Si canvia, augmenta la qualitat en 1 punt.
     */
    public void canviDePosicio() {
        if (random.nextInt(100) < 5) {
            String posicioAnterior = this.posicio;
            String novaPosicio;
            
            do {
                novaPosicio = POSICIONS[random.nextInt(POSICIONS.length)];
            } while (novaPosicio.equals(this.posicio));
            
            this.posicio = novaPosicio;
            this.qualitat += 1.0;
            
            System.out.println(String.format("  🔄 CANVI DE POSICIÓ! %s %s: %s → %s (Qualitat +1.0)", 
                    getNom(), getCognom(), posicioAnterior, novaPosicio));
        }
    }

    /**
     * Obté el número de dorsal.
     * 
     * @return El dorsal
     */
    public int getDorsal() {
        return dorsal;
    }

    /**
     * Estableix un nou número de dorsal.
     * 
     * @param dorsal El nou dorsal
     */
    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    /**
     * Obté la posició del jugador.
     * 
     * @return La posició (POR, DEF, MIG, DAV)
     */
    public String getPosicio() {
        return posicio;
    }

    /**
     * Obté la qualitat del jugador.
     * 
     * @return La qualitat (30-100)
     */
    public double getQualitat() {
        return qualitat;
    }

    /**
     * Estableix una nova qualitat.
     * 
     * @param qualitat La nova qualitat
     */
    public void setQualitat(double qualitat) {
        this.qualitat = qualitat;
    }

    /**
     * Obté el total de jugadors creats.
     * 
     * @return El nombre total de jugadors
     */
    public static int getTotalJugadors() {
        return totalJugadors;
    }
    
    /**
     * Registra un gol marcat pel jugador.
     */
    public void registrarGol() {
        this.golsMarcats++;
        actualitzarForma(0.3); // Augmenta la forma
        aumentarMotivacio(0.2); // Augmenta la motivació
    }
    
    /**
     * Registra una assistència feta pel jugador.
     */
    public void registrarAssistencia() {
        this.assistencies++;
        actualitzarForma(0.2);
        aumentarMotivacio(0.1);
    }
    
    /**
     * Registra un partit jugat.
     */
    public void registrarPartit() {
        this.partitsJugats++;
    }
    
    /**
     * Registra una targeta groga.
     */
    public void registrarTargetaGroga() {
        this.tarjetesGrogues++;
        actualitzarForma(-0.1);
    }
    
    /**
     * Registra una targeta vermella.
     */
    public void registrarTargetaVermella() {
        this.tarjetesVermelles++;
        actualitzarForma(-0.5);
        disminuirMotivacio(0.3);
    }
    
    /**
     * Actualitza la forma del jugador després d'un partit.
     * 
     * @param canvi Canvi en la forma (-1 a 1)
     */
    public void actualitzarForma(double canvi) {
        this.forma += canvi;
        
        // Limitar forma entre 0 i 10
        if (this.forma > 10.0) this.forma = 10.0;
        if (this.forma < 0.0) this.forma = 0.0;
    }
    
    /**
     * Actualitza la forma després d'un partit segons el resultat.
     * 
     * @param victoria True si l'equip ha guanyat
     * @param empat True si hi ha hagut empat
     */
    public void actualitzarFormaDespresPartit(boolean victoria, boolean empat) {
        if (victoria) {
            actualitzarForma(0.2);
            aumentarMotivacio(0.1);
        } else if (empat) {
            actualitzarForma(0.0); // No canvia
        } else {
            actualitzarForma(-0.2);
            disminuirMotivacio(0.1);
        }
    }
    
    /**
     * Augmenta la motivació del jugador.
     */
    private void aumentarMotivacio(double increment) {
        double novaMotivacio = getMotivacio() + increment;
        if (novaMotivacio > 10.0) novaMotivacio = 10.0;
        setMotivacio(novaMotivacio);
    }
    
    /**
     * Disminueix la motivació del jugador.
     */
    private void disminuirMotivacio(double decrement) {
        double novaMotivacio = getMotivacio() - decrement;
        if (novaMotivacio < 1.0) novaMotivacio = 1.0;
        setMotivacio(novaMotivacio);
    }
    
    /**
     * Obté la qualitat efectiva del jugador (qualitat * forma).
     * 
     * @return La qualitat ajustada per la forma
     */
    public double getQualitatEfectiva() {
        // La forma afecta la qualitat (±20%)
        double factorForma = 0.8 + (this.forma / 50.0); // 0.8 a 1.0
        return this.qualitat * factorForma;
    }
    
    /**
     * Obté les estadístiques del jugador en format String.
     */
    public String getEstadistiques() {
        return String.format("⚽ %d gols | 🎯 %d assist. | 🎮 %d partits | 🟨 %d | 🟥 %d | ⭐ Forma: %.1f",
                golsMarcats, assistencies, partitsJugats, tarjetesGrogues, tarjetesVermelles, forma);
    }
    
    // Getters per estadístiques
    public int getGolsMarcats() { return golsMarcats; }
    public int getAssistencies() { return assistencies; }
    public int getPartitsJugats() { return partitsJugats; }
    public int getTargetesGrogues() { return tarjetesGrogues; }
    public int getTargetesVermelles() { return tarjetesVermelles; }
    public double getForma() { return forma; }

    /**
     * Compara si dos jugadors són iguals basant-se en nom i dorsal.
     * 
     * @param obj L'objecte a comparar
     * @return true si són iguals, false altrament
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Jugador jugador = (Jugador) obj;
        return dorsal == jugador.dorsal && getNom().equals(jugador.getNom());
    }

    /**
     * Genera el hashcode basat en nom i dorsal.
     * 
     * @return El hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getNom(), dorsal);
    }

    /**
     * Retorna una representació detallada del jugador.
     * 
     * @return String amb totes les dades del jugador
     */
    @Override
    public String toString() {
        return String.format("⚽ Jugador #%d: %s %s | Posició: %s | Qualitat: %.2f | Motivació: %.2f | Sou: %.2f€",
                dorsal, getNom(), getCognom(), posicio, qualitat, getMotivacio(), getSouAnual());
    }
}
