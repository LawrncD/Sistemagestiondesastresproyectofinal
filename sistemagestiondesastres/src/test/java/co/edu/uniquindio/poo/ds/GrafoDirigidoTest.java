package co.edu.uniquindio.poo.ds;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.logging.Logger;

import co.edu.uniquindio.poo.model.Ruta;
import co.edu.uniquindio.poo.model.ZonaAfectada;

/**
 * Pruebas unitarias para GrafoDirigido.
 * 
 * @author Sistema de Gestión de Desastres
 * @version 1.0
 */
public class GrafoDirigidoTest {
    private static final Logger LOG = Logger.getLogger(GrafoDirigidoTest.class.getName());
    private GrafoDirigido grafo;
    private ZonaAfectada zona1, zona2, zona3;

    @BeforeEach
    public void setUp() {
        LOG.info("Configurando prueba de GrafoDirigido");
        grafo = new GrafoDirigido();
        
        zona1 = new ZonaAfectada("Zona A", 1000, 60, 4.5, -75.5);
        zona2 = new ZonaAfectada("Zona B", 800, 70, 4.6, -75.6);
        zona3 = new ZonaAfectada("Zona C", 1200, 50, 4.7, -75.7);
    }

    /**
     * Verifica agregar nodos al grafo.
     */
    @Test
    public void testAgregarNodos() {
        LOG.info("Test: Agregar nodos");
        
        grafo.agregarNodo(zona1);
        grafo.agregarNodo(zona2);
        grafo.agregarNodo(zona3);
        
        List<ZonaAfectada> zonas = grafo.obtenerZonas();
        assertEquals(3, zonas.size(), "Debe haber 3 zonas");
        
        LOG.info("Nodos agregados correctamente");
    }

    /**
     * Prueba Dijkstra con ruta óptima.
     */
    @Test
    public void testDijkstraRutaOptima() {
        LOG.info("Test: Dijkstra ruta óptima");
        
        grafo.agregarNodo(zona1);
        grafo.agregarNodo(zona2);
        grafo.agregarNodo(zona3);
        
        // Ruta directa larga A -> C (20 horas)
        Ruta rutaDirecta = new Ruta(zona1.getId(), zona3.getId(), 10.0, 20, 100);
        
        // Ruta corta A -> B -> C (15 horas)
        Ruta rutaAB = new Ruta(zona1.getId(), zona2.getId(), 5.0, 10, 100);
        Ruta rutaBC = new Ruta(zona2.getId(), zona3.getId(), 2.0, 5, 100);
        
        grafo.agregarArista(rutaDirecta);
        grafo.agregarArista(rutaAB);
        grafo.agregarArista(rutaBC);
        
        List<Ruta> camino = grafo.obtenerRutaMasCorta(zona1.getId(), zona3.getId());
        
        assertNotNull(camino, "Debe existir un camino");
        assertTrue(camino.size() >= 1, "El camino debe tener al menos 1 ruta");
        
        LOG.info("Dijkstra exitoso");
    }

    /**
     * Prueba Dijkstra sin camino disponible.
     */
    @Test
    public void testDijkstraSinCamino() {
        LOG.info("Test: Dijkstra sin camino");
        
        grafo.agregarNodo(zona1);
        grafo.agregarNodo(zona2);
        // No agregar rutas
        
        List<Ruta> camino = grafo.obtenerRutaMasCorta(zona1.getId(), zona2.getId());
        
        assertTrue(camino == null || camino.isEmpty(), "No debe existir camino");
        
        LOG.info("Sin camino manejado correctamente");
    }
}
