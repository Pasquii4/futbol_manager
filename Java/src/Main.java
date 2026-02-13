import model.*;
import utils.GestorFitxers;
import comparators.ComparadorJugadorQualitat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;



/**
 * Classe principal del Politècnics Football Manager.
 * Gestiona els menús i les funcionalitats de l'aplicació.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */

class DatosLogin {
    String adminPassword;
    List<Usuario> usuarios;
    
    public DatosLogin() {
        this.usuarios = new ArrayList<>();
    }
}

class Usuario {
    String nombre;
    String password;
    
    public Usuario(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }
}
public class Main {
    private static ArrayList<Equip> equips;
    private static ArrayList<Persona> mercatFitxatges;
    private static Lliga lligaActual;
    private static Scanner scanner;
    private static DatosLogin datosLogin;
    private static final String FITXER_MERCAT = "data/mercat_fitxatges.txt";
    private static final String FITXER_EQUIPS = "data/equips.txt";
    private static final String FITXER_LOGIN = "login.json";
    private static final Random random = new Random();

    /**
     * Mètode principal que inicia l'aplicació.
     * 
     * @param args Arguments de línia de comandes
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        // Carregar dades
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║      ⚽ POLITÈCNICS FOOTBALL MANAGER ⚽                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  Carregant dades...                                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        datosLogin = carregarDadesLogin();
        
        // Intentar carregar equips des del JSON primer
        equips = carregarEquipsDesDeJSON();
        if (equips.isEmpty()) {
            System.out.println("⚠️ No s'han trobat equips al JSON. Carregant des de fitxers de text...");
            equips = GestorFitxers.carregarEquips(FITXER_EQUIPS);
        }
        
        mercatFitxatges = GestorFitxers.carregarMercatFitxatges(FITXER_MERCAT);
        lligaActual = null;
        
        System.out.println(String.format("\n📊 Total jugadors creats fins ara: %d\n", Jugador.getTotalJugadors()));
        
        // Sistema de login
        boolean sortir = false;
        while (!sortir) {
            mostrarMenuLogin();
            int opcio = llegirEnter("Selecciona el teu rol: ", 0, 2);
            
            switch (opcio) {
                case 1:
                    if (autenticarAdmin()) {
                        menuAdmin();
                    }
                    break;
                case 2:
                    if (autenticarUsuari()) {
                        menuGestor();
                    }
                    break;
                case 0:
                    sortir = true;
                    System.out.println("\n👋 Fins aviat! Gràcies per utilitzar Politècnics Football Manager.\n");
                    break;
            }
        }
        
        scanner.close();
    }

    /**
     * Mostra el menú de login.
     */
    private static void mostrarMenuLogin() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║      Welcome to Politècnics Football Manager                  ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  1- Login com a Admin                                         ║");
        System.out.println("║  2- Login com a Gestor d'Equip                                ║");
        System.out.println("║  0- Sortir                                                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    /**
     * Menú principal per a administradors.
     */
    private static void menuAdmin() {
        boolean tornar = false;
        
        while (!tornar) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║      ADMIN MENU - Welcome to Politècnics Football Manager     ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║  1- Veure classificació lliga actual 🏆                       ║");
            System.out.println("║  2- Donar d'alta equip                                        ║");
            System.out.println("║  3- Donar d'alta jugador/a o entrenador/a                    ║");
            System.out.println("║  4- Consultar dades equip                                     ║");
            System.out.println("║  5- Consultar dades jugador/a equip                           ║");
            System.out.println("║  6- Gestionar lliga (sistema jornada a jornada)               ║");
            System.out.println("║  7- Realitzar sessió entrenament (del mercat fitxatges)      ║");
            System.out.println("║  8- Desar dades equips                                        ║");
            System.out.println("║  0- Sortir                                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            
            int opcio = llegirEnter("\nSelecciona una opció: ", 0, 8);
            
            switch (opcio) {
                case 1:
                    veureClassificacio();
                    break;
                case 2:
                    donarAltaEquip();
                    break;
                case 3:
                    donarAltaPersona();
                    break;
                case 4:
                    consultarDadesEquip();
                    break;
                case 5:
                    consultarDadesJugador();
                    break;
                case 6:
                    gestionarLliga();
                    break;
                case 7:
                    sessioEntrenament();
                    break;
                case 8:
                    desarDadesEquips();
                    break;
                case 0:
                    tornar = true;
                    break;
            }
        }
    }

    /**
     * Menú principal per a gestors d'equip.
     */
    private static void menuGestor() {
        boolean tornar = false;
        
        while (!tornar) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║  GESTOR MENU - Welcome to Politècnics Football Manager        ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║  1- Veure classificació lliga actual 🏆                       ║");
            System.out.println("║  2- Gestionar el meu equip ⚽                                  ║");
            System.out.println("║  3- Consultar dades equip                                     ║");
            System.out.println("║  4- Consultar dades jugador/a equip                           ║");
            System.out.println("║  5- Transferir jugador/a                                      ║");
            System.out.println("║  6- Desar dades equips                                        ║");
            System.out.println("║  0- Sortir                                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            
            int opcio = llegirEnter("\nSelecciona una opció: ", 0, 6);
            
            switch (opcio) {
                case 1:
                    veureClassificacio();
                    break;
                case 2:
                    gestionarEquip();
                    break;
                case 3:
                    consultarDadesEquip();
                    break;
                case 4:
                    consultarDadesJugador();
                    break;
                case 5:
                    transferirJugador();
                    break;
                case 6:
                    desarDadesEquips();
                    break;
                case 0:
                    tornar = true;
                    break;
            }
        }
    }

    /**
     * Submenú de gestió d'equip.
     */
    private static void gestionarEquip() {
        System.out.print("\nNom de l'equip a gestionar: ");
        String nomEquip = scanner.nextLine().trim();
        
        Equip equip = buscarEquip(nomEquip);
        if (equip == null) {
            System.out.println("❌ Equip no trobat.");
            return;
        }
        
        boolean tornar = false;
        while (!tornar) {
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║      Team Manager: " + String.format("%-44s", equip.getNom()) + "║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║  1- Donar de baixa l'equip                                    ║");
            System.out.println("║  2- Modificar president/a                                     ║");
            System.out.println("║  3- Destituir entrenador/a                                    ║");
            System.out.println("║  4- Fitxar jugador/a o entrenador/a                           ║");
            System.out.println("║  0- Sortir                                                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            
            int opcio = llegirEnter("\nSelecciona una opció: ", 0, 4);
            
            switch (opcio) {
                case 1:
                    if (donarBaixaEquip(equip)) {
                        tornar = true;
                    }
                    break;
                case 2:
                    modificarPresident(equip);
                    break;
                case 3:
                    destituirEntrenador(equip);
                    break;
                case 4:
                    fitxarPersona(equip);
                    break;
                case 0:
                    tornar = true;
                    break;
            }
        }
    }

    // ========== FUNCIONALITATS ADMIN/GESTOR ==========

    /**
     * 1- Veure classificació lliga actual.
     */
    private static void veureClassificacio() {
        if (lligaActual == null) {
            System.out.println("\n❌ No hi ha cap lliga activa. Disputa una nova lliga primer (opció 6).");
        } else {
            lligaActual.mostrarClassificacio();
        }
    }

    /**
     * 2- Donar d'alta equip.
     */
    private static void donarAltaEquip() {
        System.out.println("\n📝 DONAR D'ALTA EQUIP NOU");
        
        String nom;
        while (true) {
            System.out.print("Nom de l'equip: ");
            nom = scanner.nextLine().trim();
            
            if (buscarEquip(nom) != null) {
                System.out.println("❌ Ja existeix un equip amb aquest nom. Tria un altre.");
            } else {
                break;
            }
        }
        
        int any = llegirEnter("Any de fundació: ", 1800, 2026);
        
        System.out.print("Ciutat: ");
        String ciutat = scanner.nextLine().trim();
        
        System.out.print("Vols introduir l'estadi? (s/n): ");
        String resposta = scanner.nextLine().trim().toLowerCase();
        String estadi = null;
        if (resposta.equals("s")) {
            System.out.print("Nom de l'estadi: ");
            estadi = scanner.nextLine().trim();
        }
        
        System.out.print("Vols introduir el/la president/a? (s/n): ");
        resposta = scanner.nextLine().trim().toLowerCase();
        String president = null;
        if (resposta.equals("s")) {
            System.out.print("Nom del/de la president/a: ");
            president = scanner.nextLine().trim();
        }
        
        Equip nouEquip;
        if (estadi != null && president != null) {
            nouEquip = new Equip(nom, any, ciutat, estadi, president);
        } else {
            nouEquip = new Equip(nom, any, ciutat);
            if (estadi != null) nouEquip.setEstadi(estadi);
            if (president != null) nouEquip.setPresident(president);
        }
        
        equips.add(nouEquip);
        System.out.println("✅ Equip donat d'alta correctament!");
    }

    /**
     * 3- Donar d'alta jugador o entrenador.
     */
    private static void donarAltaPersona() {
        System.out.println("\n📝 DONAR D'ALTA JUGADOR/A O ENTRENADOR/A");
        System.out.println("1- Jugador/a");
        System.out.println("2- Entrenador/a");
        
        int tipus = llegirEnter("Selecciona: ", 1, 2);
        
        System.out.print("Nom: ");
        String nom = scanner.nextLine().trim();
        System.out.print("Cognom: ");
        String cognom = scanner.nextLine().trim();
        LocalDate data = llegirData("Data de naixement (yyyy-mm-dd): ");
        double sou = llegirDouble("Sou anual: ", 0, 100000000);
        
        if (tipus == 1) {
            // Jugador
            int dorsal = llegirEnter("Dorsal: ", 1, 99);
            
            System.out.println("Posicions disponibles: POR, DEF, MIG, DAV");
            System.out.print("Posició: ");
            String posicio = scanner.nextLine().trim().toUpperCase();
            
            double qualitat = 30 + random.nextDouble() * 70; // 30-100
            
            Jugador jugador = new Jugador(nom, cognom, data, 5.0, sou, dorsal, posicio, qualitat);
            mercatFitxatges.add(jugador);
            System.out.println("✅ Jugador/a donat d'alta al mercat amb qualitat: " + String.format("%.2f", qualitat));
            
        } else {
            // Entrenador
            int tornejos = llegirEnter("Tornejos guanyats: ", 0, 100);
            System.out.print("Ha estat seleccionador nacional? (s/n): ");
            boolean seleccionador = scanner.nextLine().trim().toLowerCase().equals("s");
            
            Entrenador entrenador = new Entrenador(nom, cognom, data, 5.0, sou, tornejos, seleccionador);
            mercatFitxatges.add(entrenador);
            System.out.println("✅ Entrenador/a donat d'alta al mercat!");
        }
    }

    /**
     * 4- Consultar dades equip.
     */
    private static void consultarDadesEquip() {
        System.out.print("\nNom de l'equip: ");
        String nom = scanner.nextLine().trim();
        
        Equip equip = buscarEquip(nom);
        if (equip == null) {
            System.out.println("❌ Equip no trobat.");
        } else {
            System.out.println(equip.toString());
        }
    }

    /**
     * 5- Consultar dades jugador d'equip.
     */
    private static void consultarDadesJugador() {
        System.out.print("\nNom de l'equip: ");
        String nomEquip = scanner.nextLine().trim();
        
        Equip equip = buscarEquip(nomEquip);
        if (equip == null) {
            System.out.println("❌ Equip no trobat.");
            return;
        }
        
        System.out.print("Nom del jugador: ");
        String nomJugador = scanner.nextLine().trim();
        int dorsal = llegirEnter("Dorsal del jugador: ", 1, 99);
        
        Jugador jugador = equip.buscarJugador(nomJugador, dorsal);
        if (jugador == null) {
            System.out.println("❌ Jugador no trobat a l'equip.");
        } else {
            System.out.println("\n" + jugador.toString());
        }
    }

    /**
     * 6- Gestionar lliga (nou sistema jornada a jornada).
     */
    private static void gestionarLliga() {
        boolean sortir = false;
        
        while (!sortir) {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║       🏆 GESTIÓ DE LLIGA 🏆                    ║");
            System.out.println("╠════════════════════════════════════════════════╣");
            
            if (lligaActual != null) {
                if (lligaActual.isLligaAcabada()) {
                    System.out.println(String.format("║ Lliga: %-39s ║", lligaActual.getNom()));
                    System.out.println("║ Estat: ✅ ACABADA                              ║");
                } else {
                    System.out.println(String.format("║ Lliga: %-39s ║", lligaActual.getNom()));
                    System.out.println(String.format("║ Jornada: %d/%d                                   ║", 
                            lligaActual.getJornadaActual(), lligaActual.getTotalJornades()));
                }
            } else {
                System.out.println("║ No hi ha lliga activa                         ║");
            }
            
            System.out.println("╠════════════════════════════════════════════════╣");
            System.out.println("║ 1- Iniciar nova lliga                         ║");
            System.out.println("║ 2- Simular propera jornada                    ║");
            System.out.println("║ 3- Veure classificació                        ║");
            System.out.println("║ 4- Veure calendari complet                    ║");
            System.out.println("║ 5- Estadístiques de jugadors                  ║");
            System.out.println("║ 0- Tornar                                     ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            
            int opcio = llegirEnter("Selecciona una opció: ", 0, 5);
            
            switch (opcio) {
                case 1:
                    iniciarNovaLliga();
                    break;
                case 2:
                    simularJornada();
                    break;
                case 3:
                    veureClassificacio();
                    break;
                case 4:
                    veureCalendari();
                    break;
                case 5:
                    veureEstadistiquesJugadors();
                    break;
                case 0:
                    sortir = true;
                    break;
            }
        }
    }
    
    /**
     * Inicia una nova lliga amb calendari generat.
     */
    private static void iniciarNovaLliga() {
        System.out.println("\n🏆 INICIAR NOVA LLIGA");
        
        if (equips.isEmpty()) {
            System.out.println("❌ No hi ha cap equip donat d'alta. Crea equips primer.");
            return;
        }
        
        System.out.println("📊 Equips disponibles: " + equips.size());
        for (int i = 0; i < equips.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + equips.get(i).getNom());
        }
        
        System.out.print("\nNom de la lliga: ");
        String nomLliga = scanner.nextLine().trim();
        
        int numEquips = llegirEnter("Nombre d'equips que participaran: ", 2, equips.size());
        
        lligaActual = new Lliga(nomLliga, numEquips);
        
        for (int i = 0; i < numEquips; i++) {
            while (true) {
                System.out.print(String.format("Equip %d/%d: ", i + 1, numEquips));
                String nomEquip = scanner.nextLine().trim();
                
                Equip equip = buscarEquip(nomEquip);
                if (equip == null) {
                    System.out.println("❌ Equip no trobat. Torna-ho a intentar.");
                } else if (!lligaActual.afegirEquip(equip)) {
                    System.out.println("❌ Aquest equip ja està a la lliga. Tria un altre.");
                } else {
                    System.out.println("✅ Equip afegit!");
                    break;
                }
            }
        }
        
        lligaActual.generarCalendari();
        System.out.println(String.format("\n🎯 Lliga preparada! %d jornades programades.", 
                lligaActual.getTotalJornades()));
        System.out.println("💡 Utilitza l'opció 2 per simular cada jornada.\n");
    }
    
    /**
     * Simula la propera jornada.
     */
    private static void simularJornada() {
        if (lligaActual == null) {
            System.out.println("\n❌ No hi ha cap lliga activa. Crea una lliga primer.\n");
            return;
        }
        
        if (!lligaActual.simularJornada()) {
            // La lliga ha acabat
            lligaActual.mostrarClassificacio();
            mostrarEstadistiquesFinals();
        } else {
            // Mostrar classificació després de la jornada
            lligaActual.mostrarClassificacio();
        }
    }
    
    /**
     * Mostra el calendari complet.
     */
    private static void veureCalendari() {
        if (lligaActual == null) {
            System.out.println("\n❌ No hi ha cap lliga activa.\n");
            return;
        }
        
        lligaActual.mostrarCalendari();
    }
    
    /**
     * Mostra estadístiques de jugadors.
     */
    private static void veureEstadistiquesJugadors() {
        if (lligaActual == null) {
            System.out.println("\n❌ No hi ha cap lliga activa.\n");
            return;
        }
        
        lligaActual.mostrarEstadistiquesJugadors();
    }
    
    /**
     * Mostra estadístiques finals quan acaba la lliga.
     */
    private static void mostrarEstadistiquesFinals() {
        Equip equipMesGolsAFavor = lligaActual.getEquipMesGolsAFavor();
        Equip equipMesGolsEnContra = lligaActual.getEquipMesGolsEnContra();
        
        System.out.println("\n📊 ESTADÍSTIQUES FINALS:");
        if (equipMesGolsAFavor != null) {
            System.out.println("⚡ Equip més golejador: " + equipMesGolsAFavor.getNom());
        }
        if (equipMesGolsEnContra != null) {
            System.out.println("🛡️ Equip més golejat: " + equipMesGolsEnContra.getNom());
        }
        
        System.out.println();
        lligaActual.mostrarEstadistiquesJugadors();
    }

    /**
     * 7- Realitzar sessió d'entrenament del mercat.
     */
    private static void sessioEntrenament() {
        System.out.println("\n💪 SESSIÓ D'ENTRENAMENT DEL MERCAT");
        System.out.println("Entrenant " + mercatFitxatges.size() + " persones...\n");
        
        for (Persona persona : mercatFitxatges) {
            persona.entrenament();
            
            if (persona instanceof Jugador) {
                Jugador jugador = (Jugador) persona;
                jugador.canviDePosicio();
            } else if (persona instanceof Entrenador) {
                Entrenador entrenador = (Entrenador) persona;
                entrenador.incrementarSou();
            }
        }
        
        System.out.println("\n✅ Sessió d'entrenament completada!");
    }

    /**
     * 8- Desar dades equips.
     */
    private static void desarDadesEquips() {
        GestorFitxers.desarEquips(equips, FITXER_EQUIPS);
    }

    // ========== FUNCIONALITATS GESTOR ==========

    /**
     * Transferir jugador entre equips.
     */
    private static void transferirJugador() {
        System.out.println("\n🔄 TRANSFERIR JUGADOR/A");
        
        System.out.print("Equip d'origen: ");
        String nomOrigen = scanner.nextLine().trim();
        Equip equipOrigen = buscarEquip(nomOrigen);
        
        if (equipOrigen == null) {
            System.out.println("❌ Equip d'origen no trobat.");
            return;
        }
        
        System.out.print("Equip de destí: ");
        String nomDesti = scanner.nextLine().trim();
        Equip equipDesti = buscarEquip(nomDesti);
        
        if (equipDesti == null) {
            System.out.println("❌ Equip de destí no trobat.");
            return;
        }
        
        System.out.print("Nom del jugador: ");
        String nom = scanner.nextLine().trim();
        int dorsal = llegirEnter("Dorsal actual: ", 1, 99);
        
        Jugador jugador = equipOrigen.buscarJugador(nom, dorsal);
        if (jugador == null) {
            System.out.println("❌ Jugador no trobat a l'equip d'origen.");
            return;
        }
        
        // Verificar dorsal disponible
        int nouDorsal = dorsal;
        while (!equipDesti.dorsalDisponible(nouDorsal)) {
            System.out.println("⚠️ El dorsal " + nouDorsal + " ja està ocupat a l'equip de destí.");
            nouDorsal = llegirEnter("Nou dorsal: ", 1, 99);
        }
        
        // Transferir
        equipOrigen.eliminarJugador(jugador);
        jugador.setDorsal(nouDorsal);
        equipDesti.afegirJugador(jugador);
        
        System.out.println("✅ Jugador transferit correctament!");
    }

    /**
     * Donar de baixa equip.
     */
    private static boolean donarBaixaEquip(Equip equip) {
        System.out.print("\n⚠️ Segur que vols eliminar l'equip " + equip.getNom() + "? (s/n): ");
        String confirmacio = scanner.nextLine().trim().toLowerCase();
        
        if (confirmacio.equals("s")) {
            equips.remove(equip);
            System.out.println("✅ Equip eliminat.");
            return true;
        } else {
            System.out.println("❌ Operació cancel·lada.");
            return false;
        }
    }

    /**
     * Modificar president d'un equip.
     */
    private static void modificarPresident(Equip equip) {
        System.out.print("\nNom del nou/nova president/a: ");
        String nouPresident = scanner.nextLine().trim();
        
        if (nouPresident.equals(equip.getPresident())) {
            System.out.println("⚠️ Aquesta persona ja és el/la president/a de l'equip.");
        } else if (equip.getPresident() == null || equip.getPresident().isEmpty()) {
            System.out.println("ℹ️ L'equip no tenia president/a assignat/da fins ara.");
        }
        
        equip.setPresident(nouPresident);
        System.out.println("✅ President/a actualitzat/da!");
    }

    /**
     * Destituir entrenador.
     */
    private static void destituirEntrenador(Equip equip) {
        if (equip.getEntrenador() == null) {
            System.out.println("❌ L'equip no té entrenador/a.");
            return;
        }
        
        System.out.print("\n⚠️ Segur que vols destituir l'entrenador/a? (s/n): ");
        String confirmacio = scanner.nextLine().trim().toLowerCase();
        
        if (confirmacio.equals("s")) {
            Entrenador entrenador = equip.getEntrenador();
            mercatFitxatges.add(entrenador);
            equip.setEntrenador(null);
            System.out.println("✅ Entrenador/a destituït/da i afegit/da al mercat!");
        } else {
            System.out.println("❌ Operació cancel·lada.");
        }
    }

    /**
     * Fitxar jugador o entrenador del mercat.
     */
    private static void fitxarPersona(Equip equip) {
        System.out.println("\n📝 FITXAR JUGADOR/A O ENTRENADOR/A");
        System.out.println("1- Jugador/a");
        System.out.println("2- Entrenador/a");
        
        int tipus = llegirEnter("Selecciona: ", 1, 2);
        
        if (tipus == 1) {
            // Mostrar jugadors disponibles ordenats
            ArrayList<Jugador> jugadorsDisponibles = new ArrayList<>();
            for (Persona p : mercatFitxatges) {
                if (p instanceof Jugador) {
                    jugadorsDisponibles.add((Jugador) p);
                }
            }
            
            if (jugadorsDisponibles.isEmpty()) {
                System.out.println("❌ No hi ha jugadors disponibles al mercat.");
                return;
            }
            
            Collections.sort(jugadorsDisponibles, new ComparadorJugadorQualitat());
            
            System.out.println("\n👥 JUGADORS DISPONIBLES:");
            for (int i = 0; i < jugadorsDisponibles.size(); i++) {
                System.out.println((i + 1) + ". " + jugadorsDisponibles.get(i).toString());
            }
            
            int seleccio = llegirEnter("\nSelecciona jugador (1-" + jugadorsDisponibles.size() + "): ", 
                                       1, jugadorsDisponibles.size());
            Jugador jugador = jugadorsDisponibles.get(seleccio - 1);
            
            // Verificar dorsal
            while (!equip.dorsalDisponible(jugador.getDorsal())) {
                System.out.println("⚠️ El dorsal " + jugador.getDorsal() + " ja està ocupat.");
                int nouDorsal = llegirEnter("Nou dorsal: ", 1, 99);
                jugador.setDorsal(nouDorsal);
            }
            
            mercatFitxatges.remove(jugador);
            equip.afegirJugador(jugador);
            System.out.println("✅ Jugador fitxat!");
            
        } else {
            // Mostrar entrenadors disponibles
            ArrayList<Entrenador> entrenadorsDisponibles = new ArrayList<>();
            for (Persona p : mercatFitxatges) {
                if (p instanceof Entrenador) {
                    entrenadorsDisponibles.add((Entrenador) p);
                }
            }
            
            if (entrenadorsDisponibles.isEmpty()) {
                System.out.println("❌ No hi ha entrenadors disponibles al mercat.");
                return;
            }
            
            System.out.println("\n👔 ENTRENADORS DISPONIBLES:");
            for (int i = 0; i < entrenadorsDisponibles.size(); i++) {
                System.out.println((i + 1) + ". " + entrenadorsDisponibles.get(i).toString());
            }
            
            int seleccio = llegirEnter("\nSelecciona entrenador (1-" + entrenadorsDisponibles.size() + "): ", 
                                       1, entrenadorsDisponibles.size());
            Entrenador entrenador = entrenadorsDisponibles.get(seleccio - 1);
            
            mercatFitxatges.remove(entrenador);
            equip.setEntrenador(entrenador);
            System.out.println("✅ Entrenador fitxat!");
        }
    }

    // ========== MÈTODES AUXILIARS ==========

    /**
     * Busca un equip per nom.
     * 
     * @param nom El nom de l'equip
     * @return L'equip si es troba, null altrament
     */
    private static Equip buscarEquip(String nom) {
        for (Equip equip : equips) {
            if (equip.getNom().equalsIgnoreCase(nom)) {
                return equip;
            }
        }
        return null;
    }

    /**
     * Llegeix un enter amb validació.
     * 
     * @param missatge El missatge a mostrar
     * @param min Valor mínim
     * @param max Valor màxim
     * @return L'enter llegit
     */
    private static int llegirEnter(String missatge, int min, int max) {
        while (true) {
            try {
                System.out.print(missatge);
                int valor = Integer.parseInt(scanner.nextLine().trim());
                if (valor >= min && valor <= max) {
                    return valor;
                } else {
                    System.out.println("⚠️ El valor ha d'estar entre " + min + " i " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid.");
            }
        }
    }

    /**
     * Llegeix un double amb validació.
     * 
     * @param missatge El missatge a mostrar
     * @param min Valor mínim
     * @param max Valor màxim
     * @return El double llegit
     */
    private static double llegirDouble(String missatge, double min, double max) {
        while (true) {
            try {
                System.out.print(missatge);
                double valor = Double.parseDouble(scanner.nextLine().trim());
                if (valor >= min && valor <= max) {
                    return valor;
                } else {
                    System.out.println("⚠️ El valor ha d'estar entre " + min + " i " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid.");
            }
        }
    }

    /**
     * Llegeix una data amb validació.
     * 
     * @param missatge El missatge a mostrar
     * @return La data llegida
     */
    /**
     * Llegeix una data amb validació.
     * 
     * @param missatge El missatge a mostrar
     * @return La data llegida
     */
    private static LocalDate llegirData(String missatge) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            try {
                System.out.print(missatge);
                String dataStr = scanner.nextLine().trim();
                return LocalDate.parse(dataStr, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Format de data incorrecte. Utilitza yyyy-mm-dd.");
            }
        }
    }
    
    /**
     * Carrega les dades de login des del fitxer login.json.
     * 
     * @return DatosLogin amb les dades carregades
     */
    private static DatosLogin carregarDadesLogin() {
        DatosLogin datos = new DatosLogin();
        
        // Intentar múltiples ubicacions del fitxer
        String[] possiblePaths = {
                "login.json",           // Directori actual
                "login.json",        // Un nivell amunt
            "../../login.json"      // Dos nivells amunt
        };
        
        boolean loaded = false;
        
        for (String path : possiblePaths) {
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String linia;
                
                while ((linia = br.readLine()) != null) {
                    jsonBuilder.append(linia.trim());
                }
                
                String json = jsonBuilder.toString();
                
                // Parse manual del JSON (simple)
                // Buscar adminPassword
                int adminPasswordStart = json.indexOf("\"adminPassword\"");
                if (adminPasswordStart != -1) {
                    int valueStart = json.indexOf(":", adminPasswordStart) + 1;
                    int valueEnd = json.indexOf(",", valueStart);
                    if (valueEnd == -1) {
                        valueEnd = json.indexOf("}", valueStart);
                    }
                    String adminPassword = json.substring(valueStart, valueEnd).trim();
                    adminPassword = adminPassword.replace("\"", "");
                    datos.adminPassword = adminPassword;
                }
                
                // Buscar usuarios array
                int usuariosStart = json.indexOf("\"usuarios\"");
                if (usuariosStart != -1) {
                    int arrayStart = json.indexOf("[", usuariosStart);
                    int arrayEnd = json.indexOf("]", arrayStart);
                    String usuariosArray = json.substring(arrayStart + 1, arrayEnd);
                    
                    // Split per objects
                    String[] userObjects = usuariosArray.split("\\},\\s*\\{");
                    
                    for (String userObj : userObjects) {
                        userObj = userObj.replace("{", "").replace("}", "").trim();
                        
                        String nombre = "";
                        String password = "";
                        
                        // Parse nombre
                        int nombreIdx = userObj.indexOf("\"nombre\"");
                        if (nombreIdx != -1) {
                            int nombreStart = userObj.indexOf(":", nombreIdx) + 1;
                            int nombreEnd = userObj.indexOf(",", nombreStart);
                            if (nombreEnd == -1) {
                                nombreEnd = userObj.length();
                            }
                            nombre = userObj.substring(nombreStart, nombreEnd).trim().replace("\"", "");
                        }
                        
                        // Parse password
                        int passwordIdx = userObj.indexOf("\"password\"");
                        if (passwordIdx != -1) {
                            int passwordStart = userObj.indexOf(":", passwordIdx) + 1;
                            password = userObj.substring(passwordStart).trim().replace("\"", "");
                        }
                        
                        if (!nombre.isEmpty() && !password.isEmpty()) {
                            datos.usuarios.add(new Usuario(nombre, password));
                        }
                    }
                }
                
                System.out.println("✅ Dades de login carregades correctament des de: " + path);
                System.out.println("📋 Usuaris disponibles: " + datos.usuarios.size());
                for (Usuario u : datos.usuarios) {
                    System.out.println("   - " + u.nombre);
                }
                
                loaded = true;
                break; // Sortir del bucle si s'ha carregat correctament
                
            } catch (IOException e) {
                // Continuar amb el següent path
            }
        }
        
        if (!loaded) {
            System.out.println("⚠️ No s'ha pogut carregar login.json des de cap ubicació.");
            System.out.println("💡 Directori de treball actual: " + System.getProperty("user.dir"));
            System.out.println("💡 SOLUCIÓ: Col·loca login.json al directori de treball o a la carpeta del projecte.");
            System.out.println("⚠️ S'utilitzaran credencials per defecte.");
            datos.adminPassword = "admin123";
            datos.usuarios.add(new Usuario("pep", "pep123"));
            datos.usuarios.add(new Usuario("maria", "futbol"));
        }
        
        return datos;
    }
    
    /**
     * Autentica un administrador.
     * 
     * @return true si l'autenticació és correcta
     */
    private static boolean autenticarAdmin() {
        System.out.println("\n🔐 LOGIN ADMIN");
        System.out.print("Contrasenya d'admin: ");
        String password = scanner.nextLine().trim();
        
        if (password.equals(datosLogin.adminPassword)) {
            System.out.println("✅ Autenticació correcta. Benvingut/da, Admin!");
            return true;
        } else {
            System.out.println("❌ Contrasenya incorrecta.");
            return false;
        }
    }
    
    /**
     * Autentica un usuari (gestor).
     * 
     * @return true si l'autenticació és correcta
     */
    private static boolean autenticarUsuari() {
        System.out.println("\n🔐 LOGIN GESTOR D'EQUIP");
        System.out.print("Nom d'usuari: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Contrasenya: ");
        String password = scanner.nextLine().trim();
        
        for (Usuario usuario : datosLogin.usuarios) {
            if (usuario.nombre.equals(nombre) && usuario.password.equals(password)) {
                System.out.println("✅ Autenticació correcta. Benvingut/da, " + nombre + "!");
                return true;
            }
        }
        
        System.out.println("❌ Credencials incorrectes.");
        return false;
    }
    
    /**
     * Carrega equips i jugadors des del fitxer jugadors_con_fotos.json.
     * 
     * @return ArrayList d'Equip carregats des del JSON
     */
    private static ArrayList<Equip> carregarEquipsDesDeJSON() {
        ArrayList<Equip> equipsCarregats = new ArrayList<>();
        
        String[] possiblePaths = {
            "jugadors_con_fotos.json",
            "../jugadors_con_fotos.json",
            "../../jugadors_con_fotos.json"
        };
        
        for (String path : possiblePaths) {
            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String linia;
                
                while ((linia = br.readLine()) != null) {
                    jsonBuilder.append(linia.trim());
                }
                
                String json = jsonBuilder.toString();
                
                // Parse l'array d'equips
                if (!json.startsWith("[")) {
                    continue;
                }
                
                // Eliminar corxets exteriors
                json = json.substring(1, json.length() - 1);
                
                // Split per equips (dividir per "},{" però tenint en compte equips amb jugadors)
                int depth = 0;
                int start = 0;
                ArrayList<String> equipStrings = new ArrayList<>();
                
                for (int i = 0; i < json.length(); i++) {
                    char c = json.charAt(i);
                    if (c == '{') depth++;
                    else if (c == '}') {
                        depth--;
                        if (depth == 0 && i > start) {
                            equipStrings.add(json.substring(start, i + 1));
                            start = i + 2; // Saltar la coma
                        }
                    }
                }
                
                // Processar cada equip
                for (String equipStr : equipStrings) {
                    try {
                        String nomEquip = extreureCamp(equipStr, "equip");
                        
                        if (nomEquip.isEmpty()) continue;
                        
                        // Crear equip (amb dades mínimes)
                        Equip equip = new Equip(nomEquip, 1900, "Barcelona");
                        
                        // Buscar l'array de jugadors
                        int jugadorsIdx = equipStr.indexOf("\"jugadors\"");
                        if (jugadorsIdx != -1) {
                            int arrayStart = equipStr.indexOf("[", jugadorsIdx);
                            int arrayEnd = trobarTancamentArray(equipStr, arrayStart);
                            
                            if (arrayStart != -1 && arrayEnd != -1) {
                                String jugadorsArray = equipStr.substring(arrayStart + 1, arrayEnd);
                                
                                // Dividir jugadors
                                ArrayList<String> jugadorStrings = dividirObjectesJSON(jugadorsArray);
                                
                                for (String jugadorStr : jugadorStrings) {
                                    try {
                                        String nomJugador = extreureCamp(jugadorStr, "nomPersona");
                                        String dorsalStr = extreureCamp(jugadorStr, "dorsal");
                                        String posicioStr = extreureCamp(jugadorStr, "posicio");
                                        String qualitatStr = extreureCamp(jugadorStr, "qualitat");
                                        
                                        if (!nomJugador.isEmpty() && !dorsalStr.isEmpty()) {
                                            int dorsal = Integer.parseInt(dorsalStr);
                                            double qualitat = qualitatStr.isEmpty() ? 70.0 : Double.parseDouble(qualitatStr);
                                            
                                            // Mapejar posicions del JSON a les del model
                                            String posicio = mapPositionFromJSON(posicioStr);
                                            
                                            // Crear jugador amb dades per defecte
                                            LocalDate dataNaixement = LocalDate.of(1995, 1, 1);
                                            double motivacio = 7.0;
                                            double sou = 50000.0 + (qualitat * 1000);
                                            
                                            Jugador jugador = new Jugador(nomJugador, "", dataNaixement, 
                                                                         motivacio, sou, dorsal, posicio, qualitat);
                                            equip.afegirJugador(jugador);
                                        }
                                    } catch (NumberFormatException e) {
                                        // Ignorar jugadors amb dades incorrectes
                                    }
                                }
                            }
                        }
                        
                        equipsCarregats.add(equip);
                        
                    } catch (Exception e) {
                        // Continuar amb el següent equip
                    }
                }
                
                System.out.println("✅ Carregats " + equipsCarregats.size() + " equips des de: " + path);
                return equipsCarregats;
                
            } catch (IOException e) {
                // Continuar amb el següent path
            }
        }
        
        return equipsCarregats;
    }
    
    /**
     * Extreu el valor d'un camp del JSON.
     */
    private static String extreureCamp(String json, String camp) {
        String searchKey = "\"" + camp + "\"";
        int idx = json.indexOf(searchKey);
        if (idx == -1) return "";
        
        int colonIdx = json.indexOf(":", idx);
        if (colonIdx == -1) return "";
        
        int valueStart = colonIdx + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }
        
        if (valueStart >= json.length()) return "";
        
        char firstChar = json.charAt(valueStart);
        if (firstChar == '"') {
            // String value
            int valueEnd = json.indexOf('"', valueStart + 1);
            if (valueEnd == -1) return "";
            return json.substring(valueStart + 1, valueEnd);
        } else {
            // Numeric or boolean value
            int valueEnd = valueStart;
            while (valueEnd < json.length() && 
                   json.charAt(valueEnd) != ',' && 
                   json.charAt(valueEnd) != '}' && 
                   json.charAt(valueEnd) != ']') {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd).trim();
        }
    }
    
    /**
     * Troba el tancament d'un array.
     */
    private static int trobarTancamentArray(String json, int start) {
        int depth = 0;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }
    
    /**
     * Divideix objectes JSON dins d'un array.
     */
    private static ArrayList<String> dividirObjectesJSON(String array) {
        ArrayList<String> objectes = new ArrayList<>();
        int depth = 0;
        int start = 0;
        
        for (int i = 0; i < array.length(); i++) {
            char c = array.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && i > start) {
                    objectes.add(array.substring(start, i + 1));
                }
            }
        }
        
        return objectes;
    }
    
    /**
     * Mapeja les posicions del JSON al format del model.
     */
    private static String mapPositionFromJSON(String posicioJSON) {
        if (posicioJSON.isEmpty()) return "MIG";
        
        posicioJSON = posicioJSON.toLowerCase();
        
        if (posicioJSON.contains("porter") || posicioJSON.contains("goalkeeper")) {
            return "POR";
        } else if (posicioJSON.contains("defensa") || posicioJSON.contains("defense")) {
            return "DEF";
        } else if (posicioJSON.contains("migcampista") || posicioJSON.contains("medio") || 
                   posicioJSON.contains("midfielder")) {
            return "MIG";
        } else if (posicioJSON.contains("davanter") || posicioJSON.contains("delantero") || 
                   posicioJSON.contains("forward") || posicioJSON.contains("atacante")) {
            return "DAV";
        }
        
        return "MIG"; // Per defecte
    }
}

