package co.edu.uniquindio.poo.ds;

import java.util.*;
import co.edu.uniquindio.poo.model.TipoRecurso;
import co.edu.uniquindio.poo.model.ZonaAfectada;

/**
 * Árbol de distribución jerárquica de recursos.
 * 
 * Organiza las zonas en una estructura de árbol para optimizar
 * la distribución de recursos basándose en prioridades, cercanía
 * y necesidades críticas.
 * 
 * @author Sistema de Gestión de Desastres - Universidad del Quindío
 * @version 1.0
 * @since 2025
 */
public class ArbolDistribucion {
    
    /** Nodo raíz del árbol (centro de distribución principal) */
    private NodoDistribucion raiz;
    
    /** Mapa de zonas por ID para acceso rápido */
    private Map<String, NodoDistribucion> nodosPorZona;
    
    /**
     * Constructor del árbol de distribución.
     */
    public ArbolDistribucion() {
        this.nodosPorZona = new HashMap<>();
    }
    
    /**
     * Establece la zona raíz (centro de distribución principal).
     * 
     * @param zona Zona que actúa como centro principal
     * @param recursosDisponibles Recursos disponibles en el centro
     */
    public void establecerCentroDistribucion(ZonaAfectada zona, Map<TipoRecurso, Integer> recursosDisponibles) {
        this.raiz = new NodoDistribucion(zona, recursosDisponibles);
        nodosPorZona.put(zona.getId(), raiz);
    }
    
    /**
     * Agrega una zona como dependiente de otra en el árbol.
     * 
     * @param zonaPadre Zona padre (distribuidora)
     * @param zonaHija Zona hija (receptora)
     * @param necesidades Recursos necesarios en la zona hija
     * @return true si se agregó exitosamente
     */
    public boolean agregarZonaDependiente(String zonaPadreId, ZonaAfectada zonaHija, Map<TipoRecurso, Integer> necesidades) {
        NodoDistribucion padre = nodosPorZona.get(zonaPadreId);
        if (padre == null) return false;
        
        NodoDistribucion nuevoNodo = new NodoDistribucion(zonaHija, necesidades);
        padre.agregarHijo(nuevoNodo);
        nodosPorZona.put(zonaHija.getId(), nuevoNodo);
        return true;
    }
    
    /**
     * Calcula la prioridad de distribución para una zona basándose en
     * nivel de riesgo, población y urgencia.
     * 
     * @param zonaId Identificador de la zona
     * @return Valor de prioridad (mayor = más urgente)
     */
    public int calcularPrioridadDistribucion(String zonaId) {
        NodoDistribucion nodo = nodosPorZona.get(zonaId);
        if (nodo == null) return 0;
        
        ZonaAfectada zona = nodo.getZona();
        
        // Prioridad = riesgo * 2 + (población / 100) + urgencia
        int prioridad = zona.getNivelDeRiesgo() * 2;
        prioridad += zona.getPoblacion() / 100;
        prioridad += zona.calcularUrgencia();
        
        return prioridad;
    }
    
    /**
     * Organiza la asignación de recursos según necesidades y prioridades.
     * 
     * @param necesidades Mapa de necesidades por zona
     * @return Mapa optimizado de asignación de recursos
     */
    public Map<String, Map<TipoRecurso, Integer>> organizarAsignacionRecursos(Map<String, Map<TipoRecurso, Integer>> necesidades) {
        Map<String, Map<TipoRecurso, Integer>> asignacion = new HashMap<>();
        
        // Ordenar zonas por prioridad
        List<String> zonasOrdenadas = new ArrayList<>(necesidades.keySet());
        zonasOrdenadas.sort((z1, z2) -> 
            Integer.compare(calcularPrioridadDistribucion(z2), calcularPrioridadDistribucion(z1))
        );
        
        // Asignar recursos según prioridad
        for (String zonaId : zonasOrdenadas) {
            asignacion.put(zonaId, necesidades.get(zonaId));
        }
        
        return asignacion;
    }
    
    /**
     * Optimiza la distribución considerando la proximidad geográfica.
     * 
     * @return Mapa de distribución optimizada por cercanía
     */
    public Map<String, Map<TipoRecurso, Integer>> optimizarDistribucionPorProximidad() {
        Map<String, Map<TipoRecurso, Integer>> distribucion = new HashMap<>();
        
        if (raiz == null) return distribucion;
        
        // Recorrer árbol en niveles (BFS) para distribuir por proximidad
        Queue<NodoDistribucion> cola = new LinkedList<>();
        cola.offer(raiz);
        
        while (!cola.isEmpty()) {
            NodoDistribucion actual = cola.poll();
            
            // Calcular recursos a distribuir desde este nodo
            Map<TipoRecurso, Integer> recursos = calcularRecursosDisponibles(actual);
            if (!recursos.isEmpty()) {
                distribucion.put(actual.getZona().getId(), recursos);
            }
            
            // Agregar hijos a la cola
            for (NodoDistribucion hijo : actual.getHijos()) {
                cola.offer(hijo);
            }
        }
        
        return distribucion;
    }
    
    /**
     * Calcula los recursos disponibles en un nodo para distribuir.
     * 
     * @param nodo Nodo del árbol
     * @return Mapa de recursos disponibles
     */
    private Map<TipoRecurso, Integer> calcularRecursosDisponibles(NodoDistribucion nodo) {
        Map<TipoRecurso, Integer> disponibles = new HashMap<>();
        
        for (Map.Entry<TipoRecurso, Integer> entry : nodo.getNecesidades().entrySet()) {
            int cantidad = entry.getValue();
            if (cantidad > 0) {
                disponibles.put(entry.getKey(), cantidad);
            }
        }
        
        return disponibles;
    }
    
    /**
     * Genera un plan de distribución completo desde el centro.
     * 
     * @return Lista de pasos de distribución en orden de ejecución
     */
    public List<String> generarPlanDistribucion() {
        List<String> plan = new ArrayList<>();
        
        if (raiz == null) {
            plan.add("⚠️ No hay centro de distribución establecido");
            return plan;
        }
        
        plan.add("📋 PLAN DE DISTRIBUCIÓN DE RECURSOS");
        plan.add("═══════════════════════════════════════");
        plan.add("🏢 Centro Principal: " + raiz.getZona().getNombre());
        
        generarPlanRecursivo(raiz, plan, 0);
        
        return plan;
    }
    
    /**
     * Genera el plan de distribución recursivamente.
     * 
     * @param nodo Nodo actual
     * @param plan Lista del plan
     * @param nivel Nivel de profundidad
     */
    private void generarPlanRecursivo(NodoDistribucion nodo, List<String> plan, int nivel) {
        String indent = "  ".repeat(nivel);
        
        if (nivel > 0) {
            plan.add(indent + "📍 " + nodo.getZona().getNombre() + 
                    " (Prioridad: " + calcularPrioridadDistribucion(nodo.getZona().getId()) + ")");
            
            if (!nodo.getNecesidades().isEmpty()) {
                for (Map.Entry<TipoRecurso, Integer> entry : nodo.getNecesidades().entrySet()) {
                    plan.add(indent + "  └─ " + entry.getKey() + ": " + entry.getValue() + " unidades");
                }
            }
        }
        
        // Procesar hijos ordenados por prioridad
        List<NodoDistribucion> hijosOrdenados = new ArrayList<>(nodo.getHijos());
        hijosOrdenados.sort((h1, h2) -> 
            Integer.compare(
                calcularPrioridadDistribucion(h2.getZona().getId()),
                calcularPrioridadDistribucion(h1.getZona().getId())
            )
        );
        
        for (NodoDistribucion hijo : hijosOrdenados) {
            generarPlanRecursivo(hijo, plan, nivel + 1);
        }
    }
    
    /**
     * Obtiene estadísticas del árbol de distribución.
     * 
     * @return Mapa con estadísticas clave
     */
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        if (raiz == null) {
            stats.put("zonas_total", 0);
            stats.put("niveles", 0);
            stats.put("recursos_tipos", 0);
            return stats;
        }
        
        stats.put("zonas_total", nodosPorZona.size());
        stats.put("niveles", calcularAltura(raiz));
        stats.put("recursos_tipos", contarTiposRecursos());
        stats.put("centro", raiz.getZona().getNombre());
        
        return stats;
    }
    
    /**
     * Calcula la altura del árbol.
     * 
     * @param nodo Nodo actual
     * @return Altura desde este nodo
     */
    private int calcularAltura(NodoDistribucion nodo) {
        if (nodo == null || nodo.getHijos().isEmpty()) {
            return 1;
        }
        
        int maxAltura = 0;
        for (NodoDistribucion hijo : nodo.getHijos()) {
            maxAltura = Math.max(maxAltura, calcularAltura(hijo));
        }
        
        return maxAltura + 1;
    }
    
    /**
     * Cuenta los tipos diferentes de recursos en el árbol.
     * 
     * @return Cantidad de tipos de recursos
     */
    private int contarTiposRecursos() {
        Set<TipoRecurso> tipos = new HashSet<>();
        
        for (NodoDistribucion nodo : nodosPorZona.values()) {
            tipos.addAll(nodo.getNecesidades().keySet());
        }
        
        return tipos.size();
    }
    
    /**
     * Muestra el árbol de distribución en consola.
     */
    public void mostrarArbol() {
        if (raiz == null) {
            System.out.println("⚠️ Árbol vacío - No hay centro de distribución");
            return;
        }
        
        System.out.println("\n🌳 ÁRBOL DE DISTRIBUCIÓN DE RECURSOS");
        System.out.println("═══════════════════════════════════════");
        mostrarNodo(raiz, "", true);
        
        System.out.println("\n📊 Estadísticas:");
        Map<String, Object> stats = obtenerEstadisticas();
        stats.forEach((k, v) -> System.out.println("  • " + k + ": " + v));
    }
    
    /**
     * Muestra un nodo del árbol recursivamente.
     * 
     * @param nodo Nodo a mostrar
     * @param prefijo Prefijo de indentación
     * @param esUltimo Si es el último hijo
     */
    private void mostrarNodo(NodoDistribucion nodo, String prefijo, boolean esUltimo) {
        System.out.println(prefijo + (esUltimo ? "└─ " : "├─ ") + 
                          nodo.getZona().getNombre() + 
                          " (Riesgo: " + nodo.getZona().getNivelDeRiesgo() + "%)");
        
        List<NodoDistribucion> hijos = nodo.getHijos();
        for (int i = 0; i < hijos.size(); i++) {
            boolean ultimo = (i == hijos.size() - 1);
            mostrarNodo(hijos.get(i), prefijo + (esUltimo ? "    " : "│   "), ultimo);
        }
    }
    
    public NodoDistribucion getRaiz() {
        return raiz;
    }
    
    public Map<String, NodoDistribucion> getNodosPorZona() {
        return nodosPorZona;
    }
    
    /**
     * Clase interna que representa un nodo del árbol de distribución.
     */
    public static class NodoDistribucion {
        private ZonaAfectada zona;
        private Map<TipoRecurso, Integer> necesidades;
        private List<NodoDistribucion> hijos;
        
        public NodoDistribucion(ZonaAfectada zona, Map<TipoRecurso, Integer> necesidades) {
            this.zona = zona;
            this.necesidades = new HashMap<>(necesidades);
            this.hijos = new ArrayList<>();
        }
        
        public void agregarHijo(NodoDistribucion hijo) {
            this.hijos.add(hijo);
        }
        
        public ZonaAfectada getZona() {
            return zona;
        }
        
        public Map<TipoRecurso, Integer> getNecesidades() {
            return necesidades;
        }
        
        public List<NodoDistribucion> getHijos() {
            return hijos;
        }
        
        public void setNecesidades(Map<TipoRecurso, Integer> necesidades) {
            this.necesidades = necesidades;
        }
    }
}