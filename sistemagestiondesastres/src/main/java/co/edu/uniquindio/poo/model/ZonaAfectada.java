package co.edu.uniquindio.poo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZonaAfectada {
    private String id;
    private String nombre;
    private int poblacion;
    private int poblacionInicial; // Para rastrear la población original
    private int nivelDeRiesgo; // 0 - 100
    private boolean evacuada; // True si la zona está completamente evacuada
    private Map<TipoRecurso, Integer> recursosAsignados;
    private List<EquipoDeRescate> equiposAsignados;
    private double lat; // Latitud (coordenada geográfica)
    private double lng; // Longitud (coordenada geográfica)
    public Object getNombre;

    public ZonaAfectada(String nombre, int poblacion, int nivelDeRiesgo) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.poblacion = poblacion;
        this.poblacionInicial = poblacion;
        this.nivelDeRiesgo = nivelDeRiesgo;
        this.evacuada = false;
        this.recursosAsignados = new HashMap<>();
        this.equiposAsignados = new ArrayList<>();
        // Coordenadas por defecto (Colombia - se pueden actualizar después)
        this.lat = 4.0 + Math.random() * 6.0; // Entre 4° y 10° N
        this.lng = -74.0 - Math.random() * 4.0; // Entre -74° y -78° W
    }

    // Constructor con coordenadas
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

    public Map<TipoRecurso, Integer> calcularRecursosFaltantes() {
        // Stub: política simple (ejemplo)
        Map<TipoRecurso, Integer> faltantes = new HashMap<>();
        // Ejemplo: si población>1000 pedir comida
        if (poblacion > 1000) faltantes.put(TipoRecurso.ALIMENTO, 500);
        return faltantes;
    }

    public void actualizarNivelRiesgo(int nuevoNivel) {
        this.nivelDeRiesgo = nuevoNivel;
    }

    public double getPorcentajeCoberturaRecursos() {
        // Stub básico: si no hay recursos devuelve 0
        if (recursosAsignados.isEmpty()) return 0.0;
        return 0.5; // placeholder
    }

    public int calcularUrgencia() {
        // Podría combinar nivelDeRiesgo y población
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
