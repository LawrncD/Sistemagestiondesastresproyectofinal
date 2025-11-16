package co.edu.uniquindio.poo.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.logging.Logger;

/**
 * Pruebas unitarias para la clase ZonaAfectada.
 * 
 * @author Sistema de Gestión de Desastres
 * @version 1.0
 */
public class ZonaAfectadaTest {
    private static final Logger LOG = Logger.getLogger(ZonaAfectadaTest.class.getName());
    private ZonaAfectada zona;

    @BeforeEach
    public void setUp() {
        LOG.info("Configurando prueba de ZonaAfectada");
        zona = new ZonaAfectada("Zona Test", 1000, 75, 4.5, -75.5);
    }

    /**
     * Verifica la creación correcta de una zona.
     */
    @Test
    public void testCreacionZona() {
        LOG.info("Test: Creación de zona");
        
        assertNotNull(zona.getId(), "El ID debe generarse");
        assertEquals("Zona Test", zona.getNombre(), "El nombre debe coincidir");
        assertEquals(1000, zona.getPoblacion(), "La población debe ser 1000");
        assertEquals(75, zona.getNivelDeRiesgo(), "El nivel de riesgo debe ser 75");
        assertFalse(zona.isEvacuada(), "La zona no debe estar evacuada");
        
        LOG.info("Creación exitosa");
    }

    /**
     * Prueba la evacuación parcial de una zona.
     */
    @Test
    public void testEvacuacionParcial() {
        LOG.info("Test: Evacuación parcial");
        
        boolean resultado = zona.evacuarPersonas(400);
        
        assertTrue(resultado, "La evacuación debe ser exitosa");
        assertEquals(600, zona.getPoblacion(), "Deben quedar 600 personas");
        assertFalse(zona.isEvacuada(), "La zona no debe estar evacuada");
        
        LOG.info("Evacuación parcial exitosa");
    }

    /**
     * Prueba la asignación de equipos de rescate.
     */
    @Test
    public void testAsignacionEquipos() {
        LOG.info("Test: Asignación de equipos");
        
        EquipoDeRescate equipo1 = new EquipoDeRescate(TipoEquipo.MEDICO, 10, "Z1", List.of("Trauma"));
        EquipoDeRescate equipo2 = new EquipoDeRescate(TipoEquipo.BOMBERO, 15, "Z1", List.of("Incendios"));
        
        zona.agregarEquipo(equipo1);
        zona.agregarEquipo(equipo2);
        
        assertEquals(2, zona.getEquiposAsignados().size(), "Deben haber 2 equipos");
        assertTrue(zona.getEquiposAsignados().contains(equipo1), "Debe contener equipo 1");
        
        LOG.info("Asignación exitosa");
    }
}
