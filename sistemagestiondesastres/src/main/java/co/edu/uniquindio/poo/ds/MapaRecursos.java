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
}