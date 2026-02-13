package model;

import java.util.*;

/**
 * Classe que representa una lliga de futbol amb jornades.
 * Permet simulació jornada a jornada amb calendari de partits.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class Lliga {
    private String nom;
    private int numEquips;
    private ArrayList<Equip> equips;
    private HashMap<String, DadesClassificacio> classificacio;
    private ArrayList<ArrayList<Partit>> jornades; // Partits organitzats per jornada
    private int jornadaActual;

    /**
     * Constructor de la lliga.
     * 
     * @param nom El nom de la lliga
     * @param numEquips El nombre d'equips que participaran
     */
    public Lliga(String nom, int numEquips) {
        this.nom = nom;
        this.numEquips = numEquips;
        this.equips = new ArrayList<>();
        this.classificacio = new HashMap<>();
        this.jornades = new ArrayList<>();
        this.jornadaActual = 0;
    }

    /**
     * Afegeix un equip a la lliga.
     * Comprova que no estigui ja afegit i que no s'excedeixi el nombre màxim.
     * 
     * @param equip L'equip a afegir
     * @return true si s'ha afegit correctament, false altrament
     */
    public boolean afegirEquip(Equip equip) {
        // Comprovar si ja està a la lliga
        for (Equip e : equips) {
            if (e.getNom().equals(equip.getNom())) {
                return false;
            }
        }
        
        // Comprovar si ja hi ha prou equips
        if (equips.size() >= numEquips) {
            return false;
        }
        
        equips.add(equip);
        classificacio.put(equip.getNom(), new DadesClassificacio());
        return true;
    }
    
    /**
     * Genera el calendari de partits per tota la lliga.
     * Cada equip juga contra tots els altres (una sola volta).
     */
    public void generarCalendari() {
        jornades.clear();
        jornadaActual = 0;
        
        ArrayList<Partit> totsElsPartits = new ArrayList<>();
        
        // Crear tots els partits
        for (int i = 0; i < equips.size(); i++) {
            for (int j = i + 1; j < equips.size(); j++) {
                totsElsPartits.add(new Partit(equips.get(i), equips.get(j)));
            }
        }
        
        // Distribuir partits en jornades
        // Intentar maximitzar partits per jornada (cada equip juga 1 cop per jornada)
        while (!totsElsPartits.isEmpty()) {
            ArrayList<Partit> jornada = new ArrayList<>();
            Set<String> equipsEnJornada = new HashSet<>();
            
            Iterator<Partit> it = totsElsPartits.iterator();
            while (it.hasNext()) {
                Partit partit = it.next();
                String nom1 = partit.getEquip1().getNom();
                String nom2 = partit.getEquip2().getNom();
                
                if (!equipsEnJornada.contains(nom1) && !equipsEnJornada.contains(nom2)) {
                    jornada.add(partit);
                    equipsEnJornada.add(nom1);
                    equipsEnJornada.add(nom2);
                    it.remove();
                }
            }
            
            if (!jornada.isEmpty()) {
                jornades.add(jornada);
            }
        }
        
        System.out.println(String.format("\n✅ Calendari generat: %d jornades\n", jornades.size()));
    }

    /**
     * Simula la propera jornada de partits.
     * 
     * @return true si s'ha simulat, false si ja s'ha acabat la lliga
     */
    public boolean simularJornada() {
        if (jornadaActual >= jornades.size()) {
            System.out.println("\n🏆 La lliga ja ha acabat!\n");
            return false;
        }
        
        ArrayList<Partit> jornada = jornades.get(jornadaActual);
        jornadaActual++;
        
        System.out.println(String.format("\n⚽ JORNADA %d de %d - %s", 
                jornadaActual, jornades.size(), nom));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Disputar tots els partits de la jornada
        for (Partit partit : jornada) {
            partit.disputar();
            System.out.println(partit.toString());
            
            // Actualitzar classificació
            actualitzarClassificacioAmbPartit(partit);
        }
        
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        if (jornadaActual >= jornades.size()) {
            System.out.println("\n🎉 LLIGA ACABADA! 🎉\n");
            mostrarCampions();
        }
        
        return true;
    }
    
    /**
     * Actualitza la classificació després d'un partit.
     */
    private void actualitzarClassificacioAmbPartit(Partit partit) {
        DadesClassificacio dades1 = classificacio.get(partit.getEquip1().getNom());
        DadesClassificacio dades2 = classificacio.get(partit.getEquip2().getNom());
        
        if (partit.getGolsEquip1() > partit.getGolsEquip2()) {
            dades1.afegirVictoria(partit.getGolsEquip1(), partit.getGolsEquip2());
            dades2.afegirDerrota(partit.getGolsEquip2(), partit.getGolsEquip1());
        } else if (partit.getGolsEquip2() > partit.getGolsEquip1()) {
            dades2.afegirVictoria(partit.getGolsEquip2(), partit.getGolsEquip1());
            dades1.afegirDerrota(partit.getGolsEquip1(), partit.getGolsEquip2());
        } else {
            dades1.afegirEmpat(partit.getGolsEquip1());
            dades2.afegirEmpat(partit.getGolsEquip2());
        }
    }
    
    /**
     * Mostra els campions de la lliga.
     */
    private void mostrarCampions() {
        List<Map.Entry<String, DadesClassificacio>> ordenada = getClassificacioOrdenada();
        
        if (!ordenada.isEmpty()) {
            String campio = ordenada.get(0).getKey();
            int punts = ordenada.get(0).getValue().getPunts();
            
            System.out.println("╔═════════════════════════════════════════════╗");
            System.out.println("║                                             ║");
            System.out.println("║            🏆 CAMPIONS DE LLIGA 🏆          ║");
            System.out.println("║                                             ║");
            System.out.println(String.format("║     %-35s      ║", campio));
            System.out.println(String.format("║            %d punts                          ║", punts));
            System.out.println("║                                             ║");
            System.out.println("╚═════════════════════════════════════════════╝\n");
        }
    }

    /**
     * Mostra la classificació ordenada per punts i diferència de gols.
     */
    public void mostrarClassificacio() {
        if (equips.isEmpty()) {
            System.out.println("❌ No hi ha cap equip a la lliga.");
            return;
        }
        
        List<Map.Entry<String, DadesClassificacio>> llista = getClassificacioOrdenada();
        
        // Mostrar classificació
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    🏆 CLASSIFICACIÓ: " + nom);
        System.out.println("╠═════╦═══════════════════════╦════════╦═══════╦═════════╦═════════╦════════╣");
        System.out.println("║ Pos ║ Equip                 ║ Punts  ║ P.J.  ║ G.F.    ║ G.C.    ║ Dif.   ║");
        System.out.println("╠═════╬═══════════════════════╬════════╬═══════╬═════════╬═════════╬════════╣");
        
        int posicio = 1;
        for (Map.Entry<String, DadesClassificacio> entry : llista) {
            String nomEquip = entry.getKey();
            DadesClassificacio dades = entry.getValue();
            
            // Afegir emojis per les primeres posicions
            String emoji = "";
            if (posicio == 1) emoji = "🥇 ";
            else if (posicio == 2) emoji = "🥈 ";
            else if (posicio == 3) emoji = "🥉 ";
            
            System.out.println(String.format("║ %2d  ║ %s%-19s ║ %6d ║ %5d ║ %7d ║ %7d ║ %+6d ║",
                    posicio, 
                    emoji,
                    nomEquip.length() > 19 ? nomEquip.substring(0, 19) : nomEquip,
                    dades.getPunts(),
                    dades.getPartitsJugats(),
                    dades.getGolsAFavor(),
                    dades.getGolsEnContra(),
                    dades.getDiferenciaGols()));
            posicio++;
        }
        
        System.out.println("╚═════╩═══════════════════════╩════════╩═══════╩═════════╩═════════╩════════╝\n");
    }
    
    /**
     * Obté la classificació ordenada.
     */
    private List<Map.Entry<String, DadesClassificacio>> getClassificacioOrdenada() {
        List<Map.Entry<String, DadesClassificacio>> llista = 
                new ArrayList<>(classificacio.entrySet());
        
        Collections.sort(llista, new Comparator<Map.Entry<String, DadesClassificacio>>() {
            @Override
            public int compare(Map.Entry<String, DadesClassificacio> e1, 
                             Map.Entry<String, DadesClassificacio> e2) {
                DadesClassificacio d1 = e1.getValue();
                DadesClassificacio d2 = e2.getValue();
                
                // Primer per punts
                int comparacioPunts = Integer.compare(d2.getPunts(), d1.getPunts());
                if (comparacioPunts != 0) {
                    return comparacioPunts;
                }
                
                // Si empaten a punts, per diferència de gols
                return Integer.compare(d2.getDiferenciaGols(), d1.getDiferenciaGols());
            }
        });
        
        return llista;
    }
    
    /**
     * Mostra el calendari complet de partits.
     */
    public void mostrarCalendari() {
        if (jornades.isEmpty()) {
            System.out.println("❌ No s'ha generat el calendari encara.");
            return;
        }
        
        System.out.println("\n📅 CALENDARI COMPLET - " + nom);
        System.out.println("════════════════════════════════════════════════\n");
        
        for (int i = 0; i < jornades.size(); i++) {
            String status = i < jornadaActual ? "✅" : (i == jornadaActual ? "▶️" : "⏸️");
            System.out.println(String.format("%s Jornada %d:", status, i + 1));
            
            for (Partit partit : jornades.get(i)) {
                if (i < jornadaActual) {
                    System.out.println("  " + partit.getResultatSimple());
                } else {
                    System.out.println(String.format("  %s vs %s", 
                            partit.getEquip1().getNom(), partit.getEquip2().getNom()));
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Mostra estadístiques de goleadors de tots els equips.
     */
    public void mostrarEstadistiquesJugadors() {
        System.out.println("\n⚽ MÀXIMS GOLEADORS");
        System.out.println("════════════════════════════════════════════════\n");
        
        // Recopilar tots els jugadors amb gols
        List<Jugador> goleadors = new ArrayList<>();
        for (Equip equip : equips) {
            for (Jugador j : equip.getJugadors()) {
                if (j.getGolsMarcats() > 0) {
                    goleadors.add(j);
                }
            }
        }
        
        // Ordenar per gols
        Collections.sort(goleadors, new Comparator<Jugador>() {
            @Override
            public int compare(Jugador j1, Jugador j2) {
                return Integer.compare(j2.getGolsMarcats(), j1.getGolsMarcats());
            }
        });
        
        // Mostrar top 10
        int limit = Math.min(10, goleadors.size());
        for (int i = 0; i < limit; i++) {
            Jugador j = goleadors.get(i);
            System.out.println(String.format("%2d. ⚽ %s - %d gols (%d assist.) - Forma: %.1f",
                    i + 1, j.getNom(), j.getGolsMarcats(), j.getAssistencies(), j.getForma()));
        }
        
        if (goleadors.isEmpty()) {
            System.out.println("No hi ha goleadors encara.");
        }
        
        System.out.println();
    }

    /**
     * Comprova si la lliga ha acabat.
     */
    public boolean isLligaAcabada() {
        return jornadaActual >= jornades.size();
    }
    
    /**
     * Obté la jornada actual.
     */
    public int getJornadaActual() {
        return jornadaActual;
    }
    
    /**
     * Obté el nombre total de jornades.
     */
    public int getTotalJornades() {
        return jornades.size();
    }

    /**
     * Obté l'equip amb més gols a favor.
     */
    public Equip getEquipMesGolsAFavor() {
        if (equips.isEmpty()) {
            return null;
        }
        
        String nomEquip = null;
        int maxGols = -1;
        
        for (Map.Entry<String, DadesClassificacio> entry : classificacio.entrySet()) {
            if (entry.getValue().getGolsAFavor() > maxGols) {
                maxGols = entry.getValue().getGolsAFavor();
                nomEquip = entry.getKey();
            }
        }
        
        for (Equip equip : equips) {
            if (equip.getNom().equals(nomEquip)) {
                return equip;
            }
        }
        
        return null;
    }

    /**
     * Obté l'equip amb més gols en contra.
     */
    public Equip getEquipMesGolsEnContra() {
        if (equips.isEmpty()) {
            return null;
        }
        
        String nomEquip = null;
        int maxGols = -1;
        
        for (Map.Entry<String, DadesClassificacio> entry : classificacio.entrySet()) {
            if (entry.getValue().getGolsEnContra() > maxGols) {
                maxGols = entry.getValue().getGolsEnContra();
                nomEquip = entry.getKey();
            }
        }
        
        for (Equip equip : equips) {
            if (equip.getNom().equals(nomEquip)) {
                return equip;
            }
        }
        
        return null;
    }

    /**
     * Obté el nom de la lliga.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Obté la llista d'equips.
     */
    public ArrayList<Equip> getEquips() {
        return equips;
    }
    
    /**
     * DEPRECATED: Utilitza generarCalendari() i simularJornada() en el seu lloc.
     */
    @Deprecated
    public void disputarLliga() {
        System.out.println("\n⚠️ Aquest mètode està obsolet. Utilitza generarCalendari() i simularJornada().\n");
        generarCalendari();
        while (!isLligaAcabada()) {
            simularJornada();
        }
    }
}
