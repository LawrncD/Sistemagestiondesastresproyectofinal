package co.edu.uniquindio.poo.ds;

import java.util.*;
import co.edu.uniquindio.poo.model.ZonaAfectada;

/**
 * Cola de prioridad para gestionar evacuaciones según el nivel de riesgo.
 * Las zonas con mayor riesgo se atienden primero.
 */
public class ColaPrioridadEvacuaciones {

    private PriorityQueue<ZonaAfectada> colaEvacuaciones;

    public ColaPrioridadEvacuaciones() {
        // Comparator: mayor riesgo -> mayor prioridad
        colaEvacuaciones = new PriorityQueue<>(
            (z1, z2) -> Integer.compare(z2.getNivelDeRiesgo(), z1.getNivelDeRiesgo())
        );
    }

    /** Agrega una zona a la cola de evacuación */
    public void registrarZonaEvacuacion(ZonaAfectada zona) {
        colaEvacuaciones.add(zona);
        System.out.println("🚨 Zona registrada para evacuación: " +
                zona.getNombre() + " (Riesgo: " + zona.getNivelDeRiesgo() + ")");
    }

    /** Atiende la siguiente zona más prioritaria */
    public ZonaAfectada procesarEvacuacion() {
        ZonaAfectada siguiente = colaEvacuaciones.poll();
        if (siguiente != null) {
            System.out.println("🚑 Evacuando zona: " + siguiente.getNombre() +
                    " (Riesgo: " + siguiente.getNivelDeRiesgo() + ")");
        } else {
            System.out.println("✅ No hay zonas pendientes de evacuación.");
        }
        return siguiente;
    }

    /** Muestra las zonas pendientes ordenadas por prioridad */
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

    public boolean estaVacia() {
        return colaEvacuaciones.isEmpty();
    }
    
}