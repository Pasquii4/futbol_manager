package persistence;

import java.io.*;
import java.util.*;

/**
 * Gestor simple de persistència sense dependències externes.
 * Usa serialització nativa de Java.
 * 
 * @author Politècnics Football Manager
 * @version 2.0
 */
public class SimpleDatabaseManager {
    private static final String DEFAULT_FILE = "game_data.ser";
    
    /**
     * Guarda un objecte serialitzable.
     */
    public static boolean saveObject(Object obj, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(obj);
            System.out.println("✅ Guardat correctament: " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error guardant: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carrega un objecte serialitzat.
     */
    public static Object loadObject(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            Object obj = ois.readObject();
            System.out.println("✅ Carregat correctament: " + filename);
            return obj;
        } catch (FileNotFoundException e) {
            System.err.println("❌ Fitxer no trobat: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Error carregant: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Comprova si existeix un fitxer.
     */
    public static boolean fileExists(String filename) {
        return new File(filename).exists();
    }
    
    /**
     * Esborra un fitxer.
     */
    public static boolean deleteFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
    
    /**
     * Crea un backup.
     */
    public static boolean createBackup(String filename) {
        if (!fileExists(filename)) {
            return false;
        }
        
        String backupName = filename.replace(".ser", "_backup.ser");
        
        try {
            File source = new File(filename);
            File dest = new File(backupName);
            
            try (FileInputStream fis = new FileInputStream(source);
                 FileOutputStream fos = new FileOutputStream(dest)) {
                
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }
            
            System.out.println("💾 Backup creat: " + backupName);
            return true;
            
        } catch (IOException e) {
            System.err.println("❌ Error creant backup: " + e.getMessage());
            return false;
        }
    }
}
