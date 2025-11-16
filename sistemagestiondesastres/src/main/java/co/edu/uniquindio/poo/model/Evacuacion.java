package co.edu.uniquindio.poo.model;

import java.util.UUID;

public class Evacuacion implements Comparable<Evacuacion> {
    private String id;
    private String zonaId;
    private int prioridad;
    private String estado; // PENDIENTE, EN_PROGRESO, COMPLETADA
    private int personasEvacuadas;
    private int personasTotales;
    private String rutaId;

    public Evacuacion(String zonaId, int personasTotales, int prioridad) {
        this.id = UUID.randomUUID().toString();
        this.zonaId = zonaId;
        this.personasTotales = personasTotales;
        this.prioridad = prioridad;
        this.estado = "PENDIENTE";
        this.personasEvacuadas = 0;
    }
    /*
     * Calcula la prioridad de la evacuación
     */

    public int calcularPrioridad() {
        return prioridad; 
    }
    /*
     * Obtiene el porcentaje de la evacuación completado
     */

    public double getPorcentajeCompletado() {
        if (personasTotales == 0) return 100.0;
        return (personasEvacuadas * 100.0) / personasTotales;
    }

    public boolean estaCompletada() {
        return personasEvacuadas >= personasTotales;
    }
    /*
     * Actualiza el progreso de la evacuación
     */

    public void actualizarProgreso(int personasEvacuadas) {
        this.personasEvacuadas += personasEvacuadas;
        if (this.personasEvacuadas >= this.personasTotales) {
            this.personasEvacuadas = this.personasTotales;
            this.estado = "COMPLETADA";
        } else {
            this.estado = "EN_PROGRESO";
        }
    }

    @Override
    public int compareTo(Evacuacion o) {
        // mayor prioridad -> antes (orden descendente por prioridad)
        return Integer.compare(o.prioridad, this.prioridad);
    }

    // getters
    public String getId() { return id; }
    public String getZonaId() { return zonaId; }
}