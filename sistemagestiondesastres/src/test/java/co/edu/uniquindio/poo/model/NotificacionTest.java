package co.edu.uniquindio.poo.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;

import co.edu.uniquindio.poo.model.Notificacion.TipoNotificacion;

/**
 * Pruebas unitarias para Notificacion.
 * 
 * @author Sistema de Gestión de Desastres
 * @version 1.0
 */
public class NotificacionTest {
    private static final Logger LOG = Logger.getLogger(NotificacionTest.class.getName());

    /**
     * Prueba creación simple de notificación.
     */
    @Test
    public void testCreacionNotificacionSimple() {
        LOG.info("Test: Creación notificación simple");
        
        Notificacion notif = new Notificacion(TipoNotificacion.RIESGO_CRITICO, "Alerta de riesgo");
        
        assertTrue(notif.getId() > 0, "El ID debe generarse");
        assertEquals("Alerta de riesgo", notif.getMensaje(), "El mensaje debe coincidir");
        assertFalse(notif.isLeida(), "No debe estar leída");
        assertEquals(TipoNotificacion.RIESGO_CRITICO, notif.getTipo(), "El tipo debe coincidir");
        
        LOG.info("Creación exitosa");
    }

    /**
     * Prueba marcar notificación como leída.
     */
    @Test
    public void testMarcarComoLeida() {
        LOG.info("Test: Marcar como leída");
        
        Notificacion notif = new Notificacion(TipoNotificacion.EVACUACION_COMPLETADA, "Evacuación lista");
        
        assertFalse(notif.isLeida(), "Inicialmente no leída");
        
        notif.marcarComoLeida();
        
        assertTrue(notif.isLeida(), "Debe estar leída");
        
        LOG.info("Marcado exitoso");
    }

    /**
     * Prueba todos los tipos de notificación.
     */
    @Test
    public void testTiposNotificacion() {
        LOG.info("Test: Tipos de notificación");
        
        Notificacion n1 = new Notificacion(TipoNotificacion.EVACUACION_COMPLETADA, "Evacuación");
        Notificacion n2 = new Notificacion(TipoNotificacion.ZONA_EVACUADA, "Zona evacuada");
        Notificacion n3 = new Notificacion(TipoNotificacion.RIESGO_CRITICO, "Riesgo crítico");
        Notificacion n4 = new Notificacion(TipoNotificacion.RECURSOS_BAJOS, "Recursos bajos");
        Notificacion n5 = new Notificacion(TipoNotificacion.EQUIPO_ASIGNADO, "Equipo asignado");
        
        assertEquals(TipoNotificacion.EVACUACION_COMPLETADA, n1.getTipo());
        assertEquals(TipoNotificacion.ZONA_EVACUADA, n2.getTipo());
        assertEquals(TipoNotificacion.RIESGO_CRITICO, n3.getTipo());
        assertEquals(TipoNotificacion.RECURSOS_BAJOS, n4.getTipo());
        assertEquals(TipoNotificacion.EQUIPO_ASIGNADO, n5.getTipo());
        
        LOG.info("Tipos validados");
    }
}
