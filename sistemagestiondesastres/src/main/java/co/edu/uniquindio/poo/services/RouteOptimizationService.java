package co.edu.uniquindio.poo.services;

import co.edu.uniquindio.poo.ds.GrafoDirigido;
import co.edu.uniquindio.poo.model.Ruta;
import co.edu.uniquindio.poo.model.ZonaAfectada;

import java.util.*;

/**
 * Servicio para cálculo de rutas óptimas usando algoritmo de Dijkstra
 */
public class RouteOptimizationService {
    
    /**
     * Encuentra la ruta más corta entre dos zonas usando Dijkstra
     * @return Lista de IDs de zonas que forman el camino, o null si no hay ruta
     */
    public static List<String> findShortestPath(GrafoDirigido grafo, String origenId, String destinoId) {
        // Verificar que ambas zonas existan
        ZonaAfectada origen = grafo.obtenerZonas().stream()
            .filter(z -> z.getId().equals(origenId))
            .findFirst()
            .orElse(null);
            
        ZonaAfectada destino = grafo.obtenerZonas().stream()
            .filter(z -> z.getId().equals(destinoId))
            .findFirst()
            .orElse(null);
            
        if (origen == null || destino == null) {
            return null;
        }
        
        // Implementación de Dijkstra
        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> previos = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>();
        
        // Inicializar distancias
        for (ZonaAfectada zona : grafo.obtenerZonas()) {
            distancias.put(zona.getId(), Double.POSITIVE_INFINITY);
        }
        distancias.put(origenId, 0.0);
        cola.offer(new NodoDistancia(origenId, 0.0));
        
        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            String actualId = actual.zonaId;
            
            if (visitados.contains(actualId)) {
                continue;
            }
            
            if (actualId.equals(destinoId)) {
                break; // Llegamos al destino
            }
            
            visitados.add(actualId);
            
            // Obtener aristas salientes
            Map<String, List<Ruta>> aristas = grafo.getAristas();
            List<Ruta> rutasSalientes = aristas.getOrDefault(actualId, new ArrayList<>());
            
            for (Ruta ruta : rutasSalientes) {
                if (!ruta.estaDisponible()) {
                    continue; // Ignorar rutas no disponibles
                }
                
                String vecinoId = ruta.getDestinoId();
                double nuevaDistancia = distancias.get(actualId) + ruta.getDistancia();
                
                if (nuevaDistancia < distancias.get(vecinoId)) {
                    distancias.put(vecinoId, nuevaDistancia);
                    previos.put(vecinoId, actualId);
                    cola.offer(new NodoDistancia(vecinoId, nuevaDistancia));
                }
            }
        }
        
        // Reconstruir camino
        if (!previos.containsKey(destinoId) && !origenId.equals(destinoId)) {
            return null; // No hay camino
        }
        
        List<String> camino = new ArrayList<>();
        String actual = destinoId;
        
        while (actual != null) {
            camino.add(0, actual);
            actual = previos.get(actual);
        }
        
        return camino.isEmpty() ? null : camino;
    }
    
    /**
     * Calcula la distancia total de un camino
     */
    public static double calculatePathDistance(GrafoDirigido grafo, List<String> camino) {
        if (camino == null || camino.size() < 2) {
            return 0.0;
        }
        
        double distanciaTotal = 0.0;
        Map<String, List<Ruta>> aristas = grafo.getAristas();
        
        for (int i = 0; i < camino.size() - 1; i++) {
            String origenId = camino.get(i);
            String destinoId = camino.get(i + 1);
            
            List<Ruta> rutas = aristas.getOrDefault(origenId, new ArrayList<>());
            Ruta rutaDirecta = rutas.stream()
                .filter(r -> r.getDestinoId().equals(destinoId))
                .findFirst()
                .orElse(null);
            
            if (rutaDirecta != null) {
                distanciaTotal += rutaDirecta.getDistancia();
            }
        }
        
        return distanciaTotal;
    }
    
    /**
     * Encuentra las N rutas más cortas alternativas
     */
    public static List<List<String>> findAlternativePaths(GrafoDirigido grafo, String origenId, String destinoId, int maxAlternativas) {
        List<List<String>> alternativas = new ArrayList<>();
        
        // Primera ruta (más corta)
        List<String> rutaPrincipal = findShortestPath(grafo, origenId, destinoId);
        if (rutaPrincipal != null) {
            alternativas.add(rutaPrincipal);
        }
        
        // TODO: Implementar k-shortest paths para alternativas
        // Por ahora solo retornamos la ruta principal
        
        return alternativas;
    }
    
    /**
     * Clase auxiliar para Dijkstra
     */
    private static class NodoDistancia implements Comparable<NodoDistancia> {
        String zonaId;
        double distancia;
        
        NodoDistancia(String zonaId, double distancia) {
            this.zonaId = zonaId;
            this.distancia = distancia;
        }
        
        @Override
        public int compareTo(NodoDistancia otro) {
            return Double.compare(this.distancia, otro.distancia);
        }
    }
}
