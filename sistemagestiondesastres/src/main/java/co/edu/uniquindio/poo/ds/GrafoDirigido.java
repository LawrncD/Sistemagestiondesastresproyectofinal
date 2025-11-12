package co.edu.uniquindio.poo.ds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import co.edu.uniquindio.poo.model.Ruta;
import co.edu.uniquindio.poo.model.ZonaAfectada;

public class GrafoDirigido {
    private Map<String, ZonaAfectada> nodos = new HashMap<>();
    private Map<String, List<Ruta>> aristas = new HashMap<>(); // origenId -> lista de rutas
    public java.util.List<co.edu.uniquindio.poo.model.ZonaAfectada> obtenerZonas() {
    return new java.util.ArrayList<>(nodos.values());
    }
    public void agregarNodo(ZonaAfectada zona) {
        nodos.put(zona.getId(), zona);
    }

    public void agregarArista(Ruta ruta) {
        aristas.computeIfAbsent(ruta.getOrigenId(), k -> new ArrayList<>()).add(ruta);
    }

    /**
     * Implementación de Dijkstra que devuelve la lista de IDs de rutas que componen el camino más corto
     * (o null si no hay camino). Prioriza por tiempo.
     */
    public List<Ruta> obtenerRutaMasCorta(String origenId, String destinoId) {
        if (!nodos.containsKey(origenId) || !nodos.containsKey(destinoId)) return null;

        // Dijkstra: distancia por tiempo
        Map<String, Double> dist = new HashMap<>();
        Map<String, Ruta> prevRuta = new HashMap<>();
        PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.dist));

        for (String id : nodos.keySet()) dist.put(id, Double.POSITIVE_INFINITY);
        dist.put(origenId, 0.0);
        pq.add(new NodeDist(origenId, 0.0));

        while (!pq.isEmpty()) {
            NodeDist curr = pq.poll();
            if (curr.dist > dist.get(curr.id)) continue;
            if (curr.id.equals(destinoId)) break;

            List<Ruta> salientes = aristas.getOrDefault(curr.id, List.of());
            for (Ruta r : salientes) {
                if (!r.estaDisponible()) continue;
                double ndist = curr.dist + r.calcularTiempoReal();
                if (ndist < dist.get(r.getDestinoId())) {
                    dist.put(r.getDestinoId(), ndist);
                    prevRuta.put(r.getDestinoId(), r);
                    pq.add(new NodeDist(r.getDestinoId(), ndist));
                }
            }
        }

        if (prevRuta.get(destinoId) == null) return null;

        // reconstruir camino en orden
        LinkedList<Ruta> camino = new LinkedList<>();
        String actual = destinoId;
        while (!actual.equals(origenId)) {
            Ruta r = prevRuta.get(actual);
            if (r == null) break;
            camino.addFirst(r);
            actual = r.getOrigenId();
        }
        return camino;
    }

    private static class NodeDist {
        String id;
        double dist;
        NodeDist(String id, double dist) { this.id = id; this.dist = dist; }
    }
    public void imprimirRutaMasCorta(String origenId, String destinoId) {
        List<Ruta> ruta = obtenerRutaMasCorta(origenId, destinoId);
    if (ruta == null || ruta.isEmpty()) {
        System.out.println("❌ No hay ruta disponible entre esas zonas.");
        return;
    }

    System.out.println("🚗 Ruta más corta encontrada:");
    for (Ruta r : ruta) {
        System.out.printf("  %s → %s (%.1f km, %.1f min)\n",
                r.getOrigenId(), r.getDestinoId(), r.getDistancia(), r.getTiempo());
    }
    double total = ruta.stream().mapToDouble(Ruta::getDistancia).sum();
    System.out.printf("Distancia total: %.1f km\n", total);
}
public Map<String, List<Ruta>> getAristas() {
    return aristas;
}
public ZonaAfectada obtenerZonaPorId(String id) {
    for (ZonaAfectada z : nodos.values()) {
        if (z.getId().equals(id)) {
            return z;
        }
    }
    return null;
}

    /**
     * Agrega una zona al grafo (alias de agregarNodo para compatibilidad con servlet)
     * @param zona La zona a agregar
     * @return true si se agregó exitosamente, false si ya existía
     */
    public boolean agregarZona(ZonaAfectada zona) {
        if (zona == null || zona.getId() == null) {
            return false;
        }

        // Verificar si la zona ya existe
        if (nodos.containsKey(zona.getId())) {
            return false;
        }

        // Agregar la zona al grafo
        nodos.put(zona.getId(), zona);

        // Inicializar lista de aristas para este nodo si no existe
        aristas.putIfAbsent(zona.getId(), new ArrayList<>());

        return true;
    }

    /**
     * Elimina una zona del grafo
     * @param zonaId El ID de la zona a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean eliminarZona(String zonaId) {
        if (!nodos.containsKey(zonaId)) {
            return false;
        }

        // Eliminar el nodo
        nodos.remove(zonaId);

        // Eliminar sus aristas salientes
        aristas.remove(zonaId);

        // Eliminar aristas que apunten a este nodo
        for (List<Ruta> rutas : aristas.values()) {
            rutas.removeIf(ruta -> ruta.getDestinoId().equals(zonaId));
        }

        return true;
    }

    /**
     * Verifica si existe una zona con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeZona(String id) {
        return nodos.containsKey(id);
    }

}