package co.edu.uniquindio.poo.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;

/**
 * Pruebas unitarias para Usuario y subclases.
 * 
 * @author Sistema de Gestión de Desastres
 * @version 1.0
 */
public class UsuarioTest {
    private static final Logger LOG = Logger.getLogger(UsuarioTest.class.getName());

    /**
     * Prueba creación de usuario Admin.
     */
    @Test
    public void testCreacionAdmin() {
        LOG.info("Test: Creación Admin");
        
        Admin admin = new Admin("admin123", "Juan Pérez", "password123", "3001234567");
        
        assertEquals("admin123", admin.getId(), "El ID debe coincidir");
        assertEquals("Juan Pérez", admin.getNombre(), "El nombre debe coincidir");
        assertEquals("ADMIN", admin.getRol(), "El rol debe ser ADMIN");
        
        LOG.info("Admin creado");
    }

    /**
     * Prueba verificación de contraseña correcta.
     */
    @Test
    public void testVerificarPasswordCorrecta() {
        LOG.info("Test: Password correcta");
        
        Admin admin = new Admin("admin123", "Juan Pérez", "password123", "3001234567");
        
        boolean resultado = admin.verificarPassword("password123");
        
        assertTrue(resultado, "La contraseña debe ser válida");
        
        LOG.info("Password verificada");
    }

    /**
     * Prueba verificación de contraseña incorrecta.
     */
    @Test
    public void testVerificarPasswordIncorrecta() {
        LOG.info("Test: Password incorrecta");
        
        OperadorDeEmergencia operador = new OperadorDeEmergencia(
            "op123", "María López", "securepass", "3009876543"
        );
        
        boolean resultado = operador.verificarPassword("wrongpassword");
        
        assertFalse(resultado, "La contraseña debe ser inválida");
        
        LOG.info("Password incorrecta manejada");
    }
}
