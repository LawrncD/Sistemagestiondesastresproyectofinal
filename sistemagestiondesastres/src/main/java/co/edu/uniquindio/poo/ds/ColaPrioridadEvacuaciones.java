package co.edu.uniquindio.poo.ds;

import java.util.*;
import co.edu.uniquindio.poo.model.ZonaAfectada;

/**
 * Cola de prioridad especializada para gestionar evacuaciones de zonas afectadas.
 * 
 * Esta estructura de datos ordena las zonas según su nivel de riesgo,
 * garantizando que las zonas más críticas sean atendidas primero.
 * Utiliza un heap binario para operaciones eficientes de inserción y extracción.
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Ordenamiento automático por nivel de riesgo (descendente)</li>
 *   <li>Operaciones de inserción en O(log n)</li>
 *   <li>Extracción del elemento prioritario en O(log n)</li>
 *   <li>Consulta del elemento más prioritario en O(1)</li>
 * </ul>
 * 
 * @author Sistema de Gestión de Desastres - Universidad del Quindío
 * @version 1.0
 * @since 2025
 */
public class ColaPrioridadEvacuaciones {

    /** Cola interna que mantiene zonas ordenadas por prioridad de riesgo */
    private PriorityQueue<ZonaAfectada> colaEvacuaciones;

    /**
     * Crea una nueva cola de prioridad para evacuaciones.
     * 
     * Configura el comparador para priorizar zonas con mayor nivel de riesgo.
     */
    public ColaPrioridadEvacuaciones() {
        colaEvacuaciones = new PriorityQueue<>(
            (z1, z2) -> Integer.compare(z2.getNivelDeRiesgo(), z1.getNivelDeRiesgo())
        );
    }

    /**
     * Registra una zona para evacuación en la cola de prioridad.
     * 
     * La zona se inserta automáticamente en la posición correspondiente
     * según su nivel de riesgo.
     * 
     * @param zona Zona afectada a registrar para evacuación
     */
    public void registrarZonaEvacuacion(ZonaAfectada zona) {
        colaEvacuaciones.add(zona);
        System.out.println("Zona registrada para evacuación: " +
                zona.getNombre() + " (Riesgo: " + zona.getNivelDeRiesgo() + ")");
    }

    /**
     * Procesa la evacuación de la zona con mayor prioridad.
     * 
     * Extrae y retorna la zona con el nivel de riesgo más alto
     * de la cola. Si la cola está vacía, retorna null.
     * 
     * @return Zona con mayor prioridad, o null si no hay zonas pendientes
     */
    public ZonaAfectada procesarEvacuacion() {
        ZonaAfectada siguiente = colaEvacuaciones.poll();
        if (siguiente != null) {
            System.out.println("Evacuando zona: " + siguiente.getNombre() +
                    " (Riesgo: " + siguiente.getNivelDeRiesgo() + ")");
        } else {
            System.out.println("No hay zonas pendientes de evacuación.");
        }
        return siguiente;
    }

    /**
     * Muestra todas las zonas pendientes ordenadas por prioridad.
     * 
     * Imprime en consola la lista de zonas esperando evacuación,
     * ordenadas de mayor a menor nivel de riesgo.
     */
    public void mostrarCola() {
        if (colaEvacuaciones.isEmpty()) {
            System.out.println("No hay zonas pendientes de evacuación.");
            return;
        }

        System.out.println("\n=== Zonas en cola de evacuación (prioridad alta primero) ===");
        colaEvacuaciones.stream()
            .sorted((z1, z2) -> Integer.compare(z2.getNivelDeRiesgo(), z1.getNivelDeRiesgo()))
            .forEach(z -> System.out.println(" - " + z.getNombre() + " (Riesgo: " + z.getNivelDeRiesgo() + ")"));
    }

    /**
     * Verifica si la cola de evacuaciones está vacía.
     * 
     * @return true si no hay zonas pendientes, false en caso contrario
     */
    public boolean estaVacia() {
        return colaEvacuaciones.isEmpty();
    }
    
}