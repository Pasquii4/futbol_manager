package utils;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Classe que gestiona la càrrega i desament de dades des de/cap a fitxers de text.
 * 
 * @author Politècnics Football Manager
 * @version 1.0
 */
public class GestorFitxers {
    
    /**
     * Carrega el mercat de fitxatges des d'un fitxer de text.
     * Format: JUGADOR|nom|cognom|dataNaixement|motivacio|sou|dorsal|posicio|qualitat
     *         ENTRENADOR|nom|cognom|dataNaixement|motivacio|sou|tornejos|seleccionador
     * 
     * @param fitxer El camí del fitxer
     * @return ArrayList de Persona amb jugadors i entrenadors
     */
    public static ArrayList<Persona> carregarMercatFitxatges(String fitxer) {
        ArrayList<Persona> mercat = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(fitxer))) {
            String linia;
            while ((linia = br.readLine()) != null) {
                linia = linia.trim();
                if (linia.isEmpty() || linia.startsWith("#")) {
                    continue; // Saltar línies buides o comentaris
                }
                
                String[] parts = linia.split("\\|");
                
                if (parts[0].equals("JUGADOR") && parts.length == 9) {
                    String nom = parts[1];
                    String cognom = parts[2];
                    LocalDate data = LocalDate.parse(parts[3]);
                    double motivacio = Double.parseDouble(parts[4]);
                    double sou = Double.parseDouble(parts[5]);
                    int dorsal = Integer.parseInt(parts[6]);
                    String posicio = parts[7];
                    double qualitat = Double.parseDouble(parts[8]);
                    
                    mercat.add(new Jugador(nom, cognom, data, motivacio, sou, dorsal, posicio, qualitat));
                    
                } else if (parts[0].equals("ENTRENADOR") && parts.length == 8) {
                    String nom = parts[1];
                    String cognom = parts[2];
                    LocalDate data = LocalDate.parse(parts[3]);
                    double motivacio = Double.parseDouble(parts[4]);
                    double sou = Double.parseDouble(parts[5]);
                    int tornejos = Integer.parseInt(parts[6]);
                    boolean seleccionador = Boolean.parseBoolean(parts[7]);
                    
                    mercat.add(new Entrenador(nom, cognom, data, motivacio, sou, tornejos, seleccionador));
                }
            }
            
            System.out.println("✅ Carregat mercat de fitxatges: " + mercat.size() + " persones.");
            
        } catch (FileNotFoundException e) {
            System.out.println("⚠️ Fitxer de mercat no trobat. Es crearà un mercat buit.");
        } catch (IOException e) {
            System.out.println("❌ Error llegint el fitxer de mercat: " + e.getMessage());
        }
        
        return mercat;
    }
    
    /**
     * Carrega els equips des d'un fitxer de text.
     * Format: EQUIP|nom|any|ciutat|estadi|president
     *         ENTRENADOR|...
     *         JUGADOR|...
     *         ---
     * 
     * @param fitxer El camí del fitxer
     * @return ArrayList d'Equip
     */
    public static ArrayList<Equip> carregarEquips(String fitxer) {
        ArrayList<Equip> equips = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(fitxer))) {
            String linia;
            Equip equipActual = null;
            
            while ((linia = br.readLine()) != null) {
                linia = linia.trim();
                if (linia.isEmpty() || linia.startsWith("#")) {
                    continue;
                }
                
                if (linia.equals("---")) {
                    if (equipActual != null) {
                        equips.add(equipActual);
                        equipActual = null;
                    }
                    continue;
                }
                
                String[] parts = linia.split("\\|");
                
                if (parts[0].equals("EQUIP") && parts.length >= 4) {
                    String nom = parts[1];
                    int any = Integer.parseInt(parts[2]);
                    String ciutat = parts[3];
                    String estadi = parts.length > 4 && !parts[4].isEmpty() ? parts[4] : null;
                    String president = parts.length > 5 && !parts[5].isEmpty() ? parts[5] : null;
                    
                    if (estadi != null && president != null) {
                        equipActual = new Equip(nom, any, ciutat, estadi, president);
                    } else {
                        equipActual = new Equip(nom, any, ciutat);
                        if (estadi != null) equipActual.setEstadi(estadi);
                        if (president != null) equipActual.setPresident(president);
                    }
                    
                } else if (parts[0].equals("ENTRENADOR") && parts.length == 8 && equipActual != null) {
                    String nom = parts[1];
                    String cognom = parts[2];
                    LocalDate data = LocalDate.parse(parts[3]);
                    double motivacio = Double.parseDouble(parts[4]);
                    double sou = Double.parseDouble(parts[5]);
                    int tornejos = Integer.parseInt(parts[6]);
                    boolean seleccionador = Boolean.parseBoolean(parts[7]);
                    
                    equipActual.setEntrenador(new Entrenador(nom, cognom, data, motivacio, sou, tornejos, seleccionador));
                    
                } else if (parts[0].equals("JUGADOR") && parts.length == 9 && equipActual != null) {
                    String nom = parts[1];
                    String cognom = parts[2];
                    LocalDate data = LocalDate.parse(parts[3]);
                    double motivacio = Double.parseDouble(parts[4]);
                    double sou = Double.parseDouble(parts[5]);
                    int dorsal = Integer.parseInt(parts[6]);
                    String posicio = parts[7];
                    double qualitat = Double.parseDouble(parts[8]);
                    
                    equipActual.afegirJugador(new Jugador(nom, cognom, data, motivacio, sou, dorsal, posicio, qualitat));
                }
            }
            
            // Afegir l'últim equip si no hi havia separador final
            if (equipActual != null) {
                equips.add(equipActual);
            }
            
            System.out.println("✅ Carregats " + equips.size() + " equips.");
            
        } catch (FileNotFoundException e) {
            System.out.println("⚠️ Fitxer d'equips no trobat. Es començarà amb llista buida.");
        } catch (IOException e) {
            System.out.println("❌ Error llegint el fitxer d'equips: " + e.getMessage());
        }
        
        return equips;
    }
    
    /**
     * Desa tots els equips en un fitxer de text.
     * 
     * @param equips La llista d'equips a desar
     * @param fitxer El camí del fitxer
     */
    public static void desarEquips(ArrayList<Equip> equips, String fitxer) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fitxer))) {
            for (Equip equip : equips) {
                // Escriure dades de l'equip
                pw.println(String.format("EQUIP|%s|%d|%s|%s|%s",
                        equip.getNom(),
                        equip.getAnyFundacio(),
                        equip.getCiutat(),
                        equip.getEstadi() != null ? equip.getEstadi() : "",
                        equip.getPresident() != null ? equip.getPresident() : ""));
                
                // Escriure entrenador
                if (equip.getEntrenador() != null) {
                    Entrenador e = equip.getEntrenador();
                    pw.println(String.format("ENTRENADOR|%s|%s|%s|%.2f|%.2f|%d|%b",
                            e.getNom(),
                            e.getCognom(),
                            e.getDataNaixement(),
                            e.getMotivacio(),
                            e.getSouAnual(),
                            e.getTornejosGuanyats(),
                            e.isSeleccionadorNacional()));
                }
                
                // Escriure jugadors
                for (Jugador j : equip.getJugadors()) {
                    pw.println(String.format("JUGADOR|%s|%s|%s|%.2f|%.2f|%d|%s|%.2f",
                            j.getNom(),
                            j.getCognom(),
                            j.getDataNaixement(),
                            j.getMotivacio(),
                            j.getSouAnual(),
                            j.getDorsal(),
                            j.getPosicio(),
                            j.getQualitat()));
                }
                
                pw.println("---");
            }
            
            System.out.println("✅ Equips desats correctament.");
            
        } catch (IOException e) {
            System.out.println("❌ Error desant els equips: " + e.getMessage());
        }
    }
}
