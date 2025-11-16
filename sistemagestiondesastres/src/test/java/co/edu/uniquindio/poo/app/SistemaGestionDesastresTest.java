package co.edu.uniquindio.poo.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;

import co.edu.uniquindio.poo.model.Admin;
import co.edu.uniquindio.poo.model.Notificacion.TipoNotificacion;

/**
 * Pruebas unitarias para SistemaGestionDesastres.
 * 
 * @author Sistema de Gestión de Desastres
 * @version 1.0
 */
public class SistemaGestionDesastresTest {
    private static final Logger LOG = Logger.getLogger(SistemaGestionDesastresTest.class.getName());
    private SistemaGestionDesastres sistema;

    @BeforeEach
    public void setUp() {
        LOG.info("Configurando prueba de SistemaGestionDesastres");
        sistema = SistemaGestionDesastres.getInstance();
    }

    /**
     * Prueba patrón Singleton.
     */
    @Test
    public void testSingletonInstancia() {
        LOG.info("Test: Singleton");
        
        SistemaGestionDesastres instancia1 = SistemaGestionDesastres.getInstance();
        SistemaGestionDesastres instancia2 = SistemaGestionDesastres.getInstance();
        
        assertSame(instancia1, instancia2, "Deben ser la misma instancia");
        
        LOG.info("Singleton validado");
    }

    /**
     * Prueba registro de usuario.
     */
    @Test
    public void testRegistrarUsuario() {
        LOG.info("Test: Registrar usuario");
        
        Admin admin = new Admin("admin999", "Admin Test", "password", "3001234567");
        boolean registrado = sistema.registrarUsuario(admin);
        
        assertTrue(registrado, "El registro debe ser exitoso");
        
        LOG.info("Usuario registrado");
    }

    /**
     * Prueba gestión de notificaciones.
     */
    @Test
    public void testGestionNotificaciones() {
        LOG.info("Test: Gestión de notificaciones");
        
        sistema.agregarNotificacion(TipoNotificacion.RIESGO_CRITICO, "Alerta 1");
        sistema.agregarNotificacion(TipoNotificacion.EVACUACION_COMPLETADA, "Alerta 2");
        
        int noLeidas = sistema.contarNotificacionesNoLeidas();
        assertTrue(noLeidas >= 2, "Debe haber al menos 2 notificaciones no leídas");
        
        // Marcar todas como leídas
        sistema.marcarTodasNotificacionesComoLeidas();
        
        int despuesMarcar = sistema.contarNotificacionesNoLeidas();
        assertTrue(despuesMarcar < noLeidas, "Debe haber menos notificaciones no leídas");
        
        LOG.info("Gestión de notificaciones exitosa");
    }
}
