package co.edu.uniquindio.poo.services;

import java.util.regex.Pattern;

/**
 * Servicio de validación de datos
 */
public class ValidationService {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?\\d{10,15}$");
    
    /**
     * Valida formato de email
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Valida formato de teléfono
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Valida contraseña (mínimo 6 caracteres)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Valida coordenadas geográficas de Colombia
     * Latitud: -4° a 15° N
     * Longitud: -82° a -66° W
     */
    public static boolean isValidColombianCoordinates(double lat, double lng) {
        return lat >= -4.0 && lat <= 15.0 && lng >= -82.0 && lng <= -66.0;
    }
    
    /**
     * Valida que un valor numérico esté en un rango
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Valida que un string no esté vacío
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Valida que un valor sea positivo
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }
    
    /**
     * Valida que un valor sea positivo
     */
    public static boolean isPositive(double value) {
        return value > 0.0;
    }
}
