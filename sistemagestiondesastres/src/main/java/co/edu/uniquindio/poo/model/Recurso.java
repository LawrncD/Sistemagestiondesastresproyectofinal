package co.edu.uniquindio.poo.model;

import java.util.UUID;

public class Recurso {
    private String id;
    private TipoRecurso tipo;
    private int cantidad;
    private String ubicacion;
    private int prioridad;

    public Recurso(TipoRecurso tipo, int cantidad, String ubicacion, int prioridad) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.ubicacion = ubicacion;
        this.prioridad = prioridad;
    }

    public boolean estaDisponible() { return cantidad > 0; }

    public int getCantidadDisponible() { return cantidad; }

    public boolean reservar(int cantidadReservar) {
        if (cantidadReservar <= 0 || cantidadReservar > cantidad) return false;
        cantidad -= cantidadReservar;
        return true;
    }

    // getters
    public String getId() { return id; }
    public TipoRecurso getTipo() { return tipo; }
    public String getUbicacion() { return ubicacion; }
}
