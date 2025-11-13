package co.edu.uniquindio.poo.ds;

import java.util.HashMap;
import java.util.Map;

import co.edu.uniquindio.poo.model.TipoRecurso;

public class MapaRecursos {
    // Mapa de ubicaciones -> recursos disponibles
    private Map<String, Map<TipoRecurso, Integer>> recursosPorUbicacion = new HashMap<>();

    /** Agrega recursos a una ubicación (si no existe, la crea). */
    public void agregarRecursosUbicacion(String ubicacion, Map<TipoRecurso, Integer> recursos) {
        recursosPorUbicacion.putIfAbsent(ubicacion, new HashMap<>());
        Map<TipoRecurso, Integer> actual = recursosPorUbicacion.get(ubicacion);
        for (var entry : recursos.entrySet()) {
            actual.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    /**
     * Agrega una cantidad específica de un tipo de recurso a una ubicación
     * @param ubicacion La ubicación donde agregar el recurso
     * @param tipo El tipo de recurso a agregar
     * @param cantidad La cantidad a agregar
     */
    public void agregarRecursos(String ubicacion, TipoRecurso tipo, int cantidad) {
        recursosPorUbicacion.putIfAbsent(ubicacion, new HashMap<>());
        Map<TipoRecurso, Integer> recursosUbicacion = recursosPorUbicacion.get(ubicacion);
        recursosUbicacion.put(tipo, recursosUbicacion.getOrDefault(tipo, 0) + cantidad);
    }

    /** Transfiere recursos entre ubicaciones si hay suficiente stock. */
    public boolean transferirRecursos(String origen, String destino, TipoRecurso tipo, int cantidad) {
        Map<TipoRecurso, Integer> recOrigen = recursosPorUbicacion.get(origen);
        Map<TipoRecurso, Integer> recDestino = recursosPorUbicacion.get(destino);
        if (recOrigen == null || recDestino == null) return false;
        int disponibles = recOrigen.getOrDefault(tipo, 0);
        if (disponibles < cantidad) return false;

        recOrigen.put(tipo, disponibles - cantidad);
        recDestino.put(tipo, recDestino.getOrDefault(tipo, 0) + cantidad);
        return true;
    }

    /** Muestra los recursos de una ubicación específica. */
    public void mostrarRecursosUbicacion(String ubicacion) {
        Map<TipoRecurso, Integer> recursos = recursosPorUbicacion.get(ubicacion);
        if (recursos == null) {
            System.out.println("No hay recursos registrados para " + ubicacion);
            return;
        }
        System.out.println("📦 Recursos en " + ubicacion + ":");
        recursos.forEach((tipo, cant) -> System.out.println("  - " + tipo + ": " + cant));
    }

    /** Muestra todos los recursos globales. */
    public void mostrarInventarioGlobal() {
        System.out.println("\n=== Inventario Global ===");
        for (String ubicacion : recursosPorUbicacion.keySet()) {
            mostrarRecursosUbicacion(ubicacion);
        }
    }

    public Map<String, Map<TipoRecurso, Integer>> getRecursosPorUbicacion() {
        return recursosPorUbicacion;
    }

    /**
     * Obtiene los recursos disponibles en una ubicación específica
     * @param ubicacion La ubicación a consultar
     * @return Mapa de recursos o null si no existe la ubicación
     */
    public Map<TipoRecurso, Integer> getRecursosUbicacion(String ubicacion) {
        return recursosPorUbicacion.get(ubicacion);
    }

    /**
     * Verifica si una ubicación tiene suficientes recursos de un tipo
     * @param ubicacion La ubicación a verificar
     * @param tipo El tipo de recurso
     * @param cantidad La cantidad requerida
     * @return true si hay suficientes recursos, false en caso contrario
     */
    public boolean tieneRecursosSuficientes(String ubicacion, TipoRecurso tipo, int cantidad) {
        Map<TipoRecurso, Integer> recursos = recursosPorUbicacion.get(ubicacion);
        if (recursos == null) return false;
        return recursos.getOrDefault(tipo, 0) >= cantidad;
    }

}