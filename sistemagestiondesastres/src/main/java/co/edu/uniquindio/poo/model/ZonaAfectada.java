package co.edu.uniquindio.poo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZonaAfectada {
    private String id;
    private String nombre;
    private int poblacion;
    private int nivelDeRiesgo; // 0 - 100
    private Map<TipoRecurso, Integer> recursosAsignados;
    public Object getNombre;

    public ZonaAfectada(String nombre, int poblacion, int nivelDeRiesgo) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.poblacion = poblacion;
        this.nivelDeRiesgo = nivelDeRiesgo;
        this.recursosAsignados = new HashMap<>();
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
    public int getNivelDeRiesgo() { return nivelDeRiesgo; }

    
}
