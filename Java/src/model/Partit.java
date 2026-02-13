package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe que representa un partit entre dos equips.
 * Simula el resultat del partit amb goleadors individuals i estad\u00edstiques detallades.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class Partit {
    private Equip equip1;
    private Equip equip2;
    private int golsEquip1;
    private int golsEquip2;
    private List<String> cronologia; // Cronologia del partit amb goleadors
    private static final Random random = new Random();

    /**
     * Constructor del partit.
     * 
     * @param equip1 El primer equip
     * @param equip2 El segon equip
     */
    public Partit(Equip equip1, Equip equip2) {
        this.equip1 = equip1;
        this.equip2 = equip2;
        this.golsEquip1 = 0;
        this.golsEquip2 = 0;
        this.cronologia = new ArrayList<>();
    }

    /**
     * Disputa el partit i calcula el resultat amb goleadors individuals.
     * Utilitza la qualitat efectiva (qualitat + forma) i motivació dels jugadors.
     */
    public void disputar() {
        cronologia.clear();
        
        // Marcar tots els jugadors com que han jugat
        for (Jugador j : equip1.getJugadors()) {
            j.registrarPartit();
        }
        for (Jugador j : equip2.getJugadors()) {
            j.registrarPartit();
        }
        
        // Calcular potencials de gol per cada equip
        double potencialGols1 = calcularPotencialGols(equip1);
        double potencialGols2 = calcularPotencialGols(equip2);
        
        // Determinar nombre de gols
        golsEquip1 = simularGols(potencialGols1);
        golsEquip2 = simularGols(potencialGols2);
        
        // Assignar goleadors
        assignarGoleadors(equip1, golsEquip1, equip2.getNom());
        assignarGoleadors(equip2, golsEquip2, equip1.getNom());
        
        // Actualitzar forma dels jugadors segons el resultat
        actualitzarFormaDespresPartit();
    }
    
    /**
     * Calcula el potencial de gols d'un equip.
     */
    private double calcularPotencialGols(Equip equip) {
        double qualitatEfectiva = 0;
        double motivacioMitjana = 0;
        int numJugadors = equip.getJugadors().size();
        
        if (numJugadors == 0) return 0;
        
        for (Jugador j : equip.getJugadors()) {
            qualitatEfectiva += j.getQualitatEfectiva();
            motivacioMitjana += j.getMotivacio();
        }
        
        qualitatEfectiva /= numJugadors;
        motivacioMitjana /= numJugadors;
        
        double factorQualitat = qualitatEfectiva / 100.0;
        double factorMotivacio = motivacioMitjana / 10.0;
        double factorAleatori = 0.7 + (random.nextDouble() * 0.6); // 0.7 - 1.3
        
        return random.nextInt(6) * factorQualitat * factorMotivacio * factorAleatori;
    }
    
    /**
     * Simula el nombre de gols basant-se en el potencial.
     */
    private int simularGols(double potencial) {
        return (int) Math.round(potencial);
    }
    
    /**
     * Assigna goleadors per als gols marcats.
     */
    private void assignarGoleadors(Equip equip, int gols, String rival) {
        if (gols == 0 || equip.getJugadors().isEmpty()) return;
        
        for (int i = 0; i < gols; i++) {
            Jugador goleador = seleccionarGoleador(equip);
            int minut = random.nextInt(90) + 1;
            
            goleador.registrarGol();
            
            // 40% probabilitat d'assistència
            if (random.nextDouble() < 0.4) {
                Jugador assistent = seleccionarAssistent(equip, goleador);
                if (assistent != null) {
                    assistent.registrarAssistencia();
                    cronologia.add(String.format("  \u26bd %d' %s (assist: %s)",
                            minut, goleador.getNom(), assistent.getNom()));
                } else {
                    cronologia.add(String.format("  \u26bd %d' %s",
                            minut, goleador.getNom()));
                }
            } else {
                cronologia.add(String.format("  \u26bd %d' %s",
                        minut, goleador.getNom()));
            }
        }
    }
    
    /**
     * Selecciona un goleador basat en posició i qualitat efectiva.
     */
    private Jugador seleccionarGoleador(Equip equip) {
        List<Jugador> jugadors = equip.getJugadors();
        double[] probabilitats = new double[jugadors.size()];
        double sumaProbabilitats = 0;
        
        for (int i = 0; i < jugadors.size(); i++) {
            Jugador j = jugadors.get(i);
            double prob = j.getQualitatEfectiva();
            
            // Ajustar per posició
            switch (j.getPosicio()) {
                case "DAV": prob *= 1.5; break;
                case "MIG": prob *= 1.0; break;
                case "DEF": prob *= 0.4; break;
                case "POR": prob *= 0.1; break;
            }
            
            probabilitats[i] = prob;
            sumaProbabilitats += prob;
        }
        
        // Seleccionar aleatòriament basat en probabilitats
        double rand = random.nextDouble() * sumaProbabilitats;
        double acumulat = 0;
        
        for (int i = 0; i < jugadors.size(); i++) {
            acumulat += probabilitats[i];
            if (rand <= acumulat) {
                return jugadors.get(i);
            }
        }
        
        return jugadors.get(0); // Fallback
    }
    
    /**
     * Selecciona un assistent (no pot ser el goleador).
     */
    private Jugador seleccionarAssistent(Equip equip, Jugador goleador) {
        List<Jugador> possibles = new ArrayList<>();
        
        for (Jugador j : equip.getJugadors()) {
            if (!j.equals(goleador) && !j.getPosicio().equals("POR")) {
                possibles.add(j);
            }
        }
        
        if (possibles.isEmpty()) return null;
        
        return possibles.get(random.nextInt(possibles.size()));
    }
    
    /**
     * Actualitza la forma dels jugadors després del partit.
     */
    private void actualitzarFormaDespresPartit() {
        boolean victoria1 = golsEquip1 > golsEquip2;
        boolean victoria2 = golsEquip2 > golsEquip1;
        boolean empat = golsEquip1 == golsEquip2;
        
        for (Jugador j : equip1.getJugadors()) {
            j.actualitzarFormaDespresPartit(victoria1, empat);
        }
        
        for (Jugador j : equip2.getJugadors()) {
            j.actualitzarFormaDespresPartit(victoria2, empat);
        }
    }

    /**
     * Obté l'equip guanyador.
     * 
     * @return L'equip guanyador, o null si hi ha empat
     */
    public Equip getGuanyador() {
        if (golsEquip1 > golsEquip2) {
            return equip1;
        } else if (golsEquip2 > golsEquip1) {
            return equip2;
        }
        return null; // Empat
    }

    /**
     * Obté el primer equip.
     * 
     * @return El primer equip
     */
    public Equip getEquip1() {
        return equip1;
    }

    /**
     * Obté el segon equip.
     * 
     * @return El segon equip
     */
    public Equip getEquip2() {
        return equip2;
    }

    /**
     * Obté els gols del primer equip.
     * 
     * @return Els gols de l'equip 1
     */
    public int getGolsEquip1() {
        return golsEquip1;
    }

    /**
     * Obté els gols del segon equip.
     * 
     * @return Els gols de l'equip 2
     */
    public int getGolsEquip2() {
        return golsEquip2;
    }
    
    /**
     * Obté la cronologia del partit amb goleadors.
     * 
     * @return Llista de strings amb els events del partit
     */
    public List<String> getCronologia() {
        return new ArrayList<>(cronologia);
    }

    /**
     * Retorna una representació detallada del resultat del partit.
     * 
     * @return String amb el resultat i goleadors
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %d - %d %s\n", 
                equip1.getNom(), golsEquip1, golsEquip2, equip2.getNom()));
        
        for (String event : cronologia) {
            sb.append(event).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Retorna només el resultat sense cronologia.
     */
    public String getResultatSimple() {
        return String.format("%s %d - %d %s", 
                equip1.getNom(), golsEquip1, golsEquip2, equip2.getNom());
    }
}
