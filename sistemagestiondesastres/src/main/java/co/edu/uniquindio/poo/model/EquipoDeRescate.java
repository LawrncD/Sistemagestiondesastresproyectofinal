package co.edu.uniquindio.poo.model;

import java.util.List;
import java.util.UUID;

public class EquipoDeRescate {
    private String id;
    private TipoEquipo tipo;
    private int miembros;
    private String ubicacionActual;
    private boolean disponible;
    private List<String> especialidades;

    public EquipoDeRescate(TipoEquipo tipo, int miembros, String ubicacionActual, List<String> especialidades) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.miembros = miembros;
        this.ubicacionActual = ubicacionActual;
        this.disponible = true;
        this.especialidades = especialidades;
    }

    public boolean asignarAZona(String zonaId) {
        if (!disponible) return false;
        this.ubicacionActual = zonaId;
        this.disponible = false;
        return true;
    }

    public boolean estaDisponible() { return disponible; }

    public void reportarEstado(String estado) {
        // stub: log o actualizar estado
        System.out.printf("Equipo %s reporta: %s%n", id, estado);
    }

    public String getId() { return id; }
}
