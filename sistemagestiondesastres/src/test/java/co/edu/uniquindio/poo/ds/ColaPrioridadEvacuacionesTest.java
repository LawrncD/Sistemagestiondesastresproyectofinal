package co.edu.uniquindio.poo.ds;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;

import co.edu.uniquindio.poo.model.ZonaAfectada;

/**
 * Pruebas unitarias para ColaPrioridadEvacuaciones.
 * 
 * @author Sistema de Gestión de Desastres
 * @version 1.0
 */
public class ColaPrioridadEvacuacionesTest {
    private static final Logger LOG = Logger.getLogger(ColaPrioridadEvacuacionesTest.class.getName());
    private ColaPrioridadEvacuaciones cola;

    @BeforeEach
    public void setUp() {
        LOG.info("Configurando prueba de ColaPrioridadEvacuaciones");
        cola = new ColaPrioridadEvacuaciones();
    }

    /**
     * Prueba estado inicial de la cola.
     */
    @Test
    public void testColaInicial() {
        LOG.info("Test: Estado inicial");
        
        assertTrue(cola.estaVacia(), "La cola debe estar vacía");
        assertNull(cola.procesarEvacuacion(), "No debe haber evacuaciones");
        
        LOG.info("Estado inicial correcto");
    }

    /**
     * Prueba registrar una zona para evacuación.
     */
    @Test
    public void testRegistrarZonaEvacuacion() {
        LOG.info("Test: Registrar zona evacuación");
        
        ZonaAfectada zona = new ZonaAfectada("Zona Test", 1000, 90, 4.5, -75.5);
        
        cola.registrarZonaEvacuacion(zona);
        
        assertFalse(cola.estaVacia(), "La cola no debe estar vacía");
        assertEquals(zona, cola.procesarEvacuacion(), "Debe devolver la zona");
        
        LOG.info("Zona registrada");
    }

    /**
     * Prueba orden por prioridad.
     */
    @Test
    public void testOrdenPorPrioridad() {
        LOG.info("Test: Orden por prioridad");
        
        ZonaAfectada zona1 = new ZonaAfectada("Zona 1", 1000, 50, 4.5, -75.5);
        ZonaAfectada zona2 = new ZonaAfectada("Zona 2", 2000, 90, 4.6, -75.6);
        ZonaAfectada zona3 = new ZonaAfectada("Zona 3", 1500, 70, 4.7, -75.7);
        
        cola.registrarZonaEvacuacion(zona1);
        cola.registrarZonaEvacuacion(zona2);
        cola.registrarZonaEvacuacion(zona3);
        
        // Debe salir primero la de mayor urgencia (zona2 con 90 de riesgo)
        ZonaAfectada primera = cola.procesarEvacuacion();
        assertEquals(zona2, primera, "Debe salir mayor urgencia");
        
        LOG.info("Orden correcto");
    }
}
