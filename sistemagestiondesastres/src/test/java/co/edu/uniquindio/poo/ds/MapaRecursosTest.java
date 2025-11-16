package co.edu.uniquindio.poo.ds;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.logging.Logger;

import co.edu.uniquindio.poo.model.TipoRecurso;

/**
 * Pruebas unitarias para MapaRecursos.
 * 
 * @author Sistema de Gestión de Desastres
 * @version 1.0
 */
public class MapaRecursosTest {
    private static final Logger LOG = Logger.getLogger(MapaRecursosTest.class.getName());
    private MapaRecursos mapa;

    @BeforeEach
    public void setUp() {
        LOG.info("Configurando prueba de MapaRecursos");
        mapa = new MapaRecursos();
    }

    /**
     * Prueba agregar recursos a una ubicación nueva.
     */
    @Test
    public void testAgregarRecursosNuevaUbicacion() {
        LOG.info("Test: Agregar recursos a ubicación nueva");
        
        mapa.agregarRecursos("Almacen1", TipoRecurso.AGUA, 100);
        
        int cantidad = mapa.getRecursosUbicacion("Almacen1").getOrDefault(TipoRecurso.AGUA, 0);
        assertEquals(100, cantidad, "Debe haber 100 unidades de agua");
        
        LOG.info("Recursos agregados correctamente");
    }

    /**
     * Prueba transferencia exitosa de recursos.
     */
    @Test
    public void testTransferenciaExitosa() {
        LOG.info("Test: Transferencia exitosa");
        
        mapa.agregarRecursos("Almacen1", TipoRecurso.ALIMENTO, 200);
        mapa.agregarRecursos("Almacen2", TipoRecurso.ALIMENTO, 0);
        
        boolean transferido = mapa.transferirRecursos("Almacen1", "Almacen2", TipoRecurso.ALIMENTO, 80);
        
        assertTrue(transferido, "La transferencia debe ser exitosa");
        assertEquals(120, mapa.getRecursosUbicacion("Almacen1").get(TipoRecurso.ALIMENTO), "Deben quedar 120");
        assertEquals(80, mapa.getRecursosUbicacion("Almacen2").get(TipoRecurso.ALIMENTO), "Debe haber 80");
        
        LOG.info("Transferencia exitosa");
    }

    /**
     * Prueba transferencia con recursos insuficientes.
     */
    @Test
    public void testTransferenciaInsuficiente() {
        LOG.info("Test: Transferencia insuficiente");
        
        mapa.agregarRecursos("Almacen1", TipoRecurso.MEDICINA, 50);
        mapa.agregarRecursos("Almacen2", TipoRecurso.MEDICINA, 0);
        
        boolean transferido = mapa.transferirRecursos("Almacen1", "Almacen2", TipoRecurso.MEDICINA, 100);
        
        assertFalse(transferido, "La transferencia debe fallar");
        assertEquals(50, mapa.getRecursosUbicacion("Almacen1").get(TipoRecurso.MEDICINA), "Debe quedar igual");
        
        LOG.info("Recursos insuficientes manejado");
    }
}
