package co.edu.uniquindio.poo.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Servicio de seguridad para encriptación de contraseñas
 */
public class SecurityService {
    
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    
    /**
     * Genera un salt aleatorio
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hashea una contraseña con salt
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            String combined = password + salt;
            byte[] hash = digest.digest(combined.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }
    
    /**
     * Verifica una contraseña contra su hash
     */
    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        String newHash = hashPassword(password, salt);
        return newHash.equals(hashedPassword);
    }
    
    /**
     * Genera un hash completo (incluye salt automáticamente)
     */
    public static String[] hashPasswordWithSalt(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return new String[]{hash, salt};
    }
}
