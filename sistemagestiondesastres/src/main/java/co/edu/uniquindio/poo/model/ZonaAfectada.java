package co.edu.uniquindio.poo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Representa una zona geográfica afectada por un desastre natural.
 * 
 * Esta clase modela una región que requiere atención de emergencia,
 * manteniendo información sobre población, nivel de riesgo, recursos
 * asignados, equipos de rescate y estado de evacuación.
 * 
 * <p>Atributos principales:</p>
 * <ul>
 *   <li>Identificación única y nombre descriptivo</li>
 *   <li>Población actual y original para rastrear evacuaciones</li>
 *   <li>Nivel de riesgo en escala de 0 a 100</li>
 *   <li>Coordenadas geográficas (latitud y longitud)</li>
 *   <li>Recursos asignados por tipo</li>
 *   <li>Equipos de rescate desplegados</li>
 *   <li>Estado de evacuación completa</li>
 * </ul>
 * 
 * @author Sistema de Gestión de Desastres - Universidad del Quindío
 * @version 1.0
 * @since 2025
 */
public class ZonaAfectada {
    /** Identificador único generado automáticamente */
    private String id;
    
    /** Nombre descriptivo de la zona afectada */
    private String nombre;
    
    /** Población actual en la zona (se reduce con evacuaciones) */
    private int poblacion;
    
    /** Población original antes de evacuaciones */
    private int poblacionInicial;
    
    /** Nivel de riesgo en escala de 0 (mínimo) a 100 (máximo) */
    private int nivelDeRiesgo;
    
    /** Indica si la zona está completamente evacuada */
    private boolean evacuada;
    
    /** Mapa de recursos asignados a la zona por tipo */
    private Map<TipoRecurso, Integer> recursosAsignados;
    
    /** Lista de equipos de rescate desplegados en la zona */
    private List<EquipoDeRescate> equiposAsignados;
    
    /** Latitud de la ubicación geográfica */
    private double lat;
    
    /** Longitud de la ubicación geográfica */
    private double lng;
    
    public Object getNombre;

    /**
     * Crea una zona afectada con coordenadas aleatorias en Colombia.
     * 
     * @param nombre Nombre descriptivo de la zona
     * @param poblacion Número de habitantes en la zona
     * @param nivelDeRiesgo Nivel de riesgo entre 0 y 100
     */
    public ZonaAfectada(String nombre, int poblacion, int nivelDeRiesgo) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.poblacion = poblacion;
        this.poblacionInicial = poblacion;
        this.nivelDeRiesgo = nivelDeRiesgo;
        this.evacuada = false;
        this.recursosAsignados = new HashMap<>();
        this.equiposAsignados = new ArrayList<>();
        this.lat = 4.0 + Math.random() * 6.0;
        this.lng = -74.0 - Math.random() * 4.0;
    }

    /**
     * Crea una zona afectada con coordenadas específicas.
     * 
     * @param nombre Nombre descriptivo de la zona
     * @param poblacion Número de habitantes en la zona
     * @param nivelDeRiesgo Nivel de riesgo entre 0 y 100
     * @param lat Latitud de la ubicación
     * @param lng Longitud de la ubicación
     */
    public ZonaAfectada(String nombre, int poblacion, int nivelDeRiesgo, double lat, double lng) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.poblacion = poblacion;
        this.poblacionInicial = poblacion;
        this.nivelDeRiesgo = nivelDeRiesgo;
        this.evacuada = false;
        this.recursosAsignados = new HashMap<>();
        this.equiposAsignados = new ArrayList<>();
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Calcula los recursos faltantes necesarios para la zona.
     * 
     * Aplica una política simple basada en la población actual
     * para determinar las necesidades de recursos.
     * 
     * @return Mapa con los tipos de recursos y cantidades faltantes
     */
    public Map<TipoRecurso, Integer> calcularRecursosFaltantes() {
        Map<TipoRecurso, Integer> faltantes = new HashMap<>();
        if (poblacion > 1000) faltantes.put(TipoRecurso.ALIMENTO, 500);
        return faltantes;
    }

    /**
     * Actualiza el nivel de riesgo de la zona.
     * 
     * @param nuevoNivel Nuevo nivel de riesgo entre 0 y 100
     */
    public void actualizarNivelRiesgo(int nuevoNivel) {
        this.nivelDeRiesgo = nuevoNivel;
    }

    /**
     * Calcula el porcentaje de cobertura de recursos en la zona.
     * 
     * @return Valor entre 0.0 y 1.0 representando el porcentaje de cobertura
     */
    public double getPorcentajeCoberturaRecursos() {
        if (recursosAsignados.isEmpty()) return 0.0;
        return 0.5;
    }

    /**
     * Calcula el nivel de urgencia considerando riesgo y población.
     * 
     * @return Valor numérico representando la urgencia de atención
     */
    public int calcularUrgencia() {
        return nivelDeRiesgo + (poblacion / 100);
    }

    // getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPoblacion() { return poblacion; }
    public int getPoblacionInicial() { return poblacionInicial; }
    public int getNivelDeRiesgo() { return nivelDeRiesgo; }
    public boolean isEvacuada() { return evacuada; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    public Map<TipoRecurso, Integer> getRecursosAsignados() {
        return recursosAsignados;
    }

    public List<EquipoDeRescate> getEquiposAsignados() {
        return equiposAsignados;
    }

    public void agregarEquipo(EquipoDeRescate equipo) {
        this.equiposAsignados.add(equipo);
    }

    public void removerEquipo(String equipoId) {
        this.equiposAsignados.removeIf(e -> e.getId().equals(equipoId));
    }

    // Setters (útiles para actualizaciones)
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPoblacion(int poblacion) {
        this.poblacion = poblacion;
    }

    public void setNivelDeRiesgo(int nivelDeRiesgo) {
        if (nivelDeRiesgo >= 0 && nivelDeRiesgo <= 100) {
            this.nivelDeRiesgo = nivelDeRiesgo;
        }
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return String.format("ZonaAfectada{id='%s', nombre='%s', poblacion=%d, nivelDeRiesgo=%d, recursos=%s}",
                id, nombre, poblacion, nivelDeRiesgo, recursosAsignados);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZonaAfectada that = (ZonaAfectada) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Elimina o reduce la cantidad de un recurso
     * @param tipo Tipo de recurso
     * @param cantidad Cantidad a reducir
     * @return true si se pudo reducir, false si no había suficiente
     */
    public boolean reducirRecurso(TipoRecurso tipo, int cantidad) {
        int actual = recursosAsignados.getOrDefault(tipo, 0);
        if (actual < cantidad) {
            return false;
        }
        recursosAsignados.put(tipo, actual - cantidad);
        return true;
    }

    /**
     * Evacua una cantidad específica de personas de la zona
     * @param cantidadEvacuados Número de personas evacuadas
     * @return true si la evacuación fue exitosa
     */
    public boolean evacuarPersonas(int cantidadEvacuados) {
        if (cantidadEvacuados <= 0 || cantidadEvacuados > poblacion) {
            return false;
        }
        
        this.poblacion -= cantidadEvacuados;
        
        // Si la población llega a 0, marcar zona como evacuada
        if (this.poblacion <= 0) {
            this.poblacion = 0;
            this.evacuada = true;
        }
        
        return true;
    }

    /**
     * Marca la zona como evacuada completamente
     */
    public void marcarComoEvacuada() {
        this.poblacion = 0;
        this.evacuada = true;
    }

    /**
     * Restaura la población de la zona (en caso de error o reversar evacuación)
     */
    public void restaurarPoblacion() {
        this.poblacion = this.poblacionInicial;
        this.evacuada = false;
    }

    
}
