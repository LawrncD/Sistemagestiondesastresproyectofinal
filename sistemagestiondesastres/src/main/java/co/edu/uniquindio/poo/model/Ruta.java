package co.edu.uniquindio.poo.model;

import java.util.UUID;

public class Ruta {
    private String id;
    private String origenId;
    private String destinoId;
    private double tiempo; 
    private double distancia; 
    private boolean disponible;
    private int capacidad; 

    public Ruta(String origenId, String destinoId, double distancia, double tiempo, int capacidad) {
        this.id = UUID.randomUUID().toString();
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.distancia = distancia;
        this.tiempo = tiempo;
        this.disponible = true;
        this.capacidad = capacidad;
    }

    public double calcularTiempoReal() {
        // En fases avanzadas se puede ajustar por congestion
        return tiempo;
    }

    public boolean estaDisponible() { return disponible; }
    public int getCapacidadDisponible() { return capacidad; }

    // getters
    public String getId() { return id; }
    public String getOrigenId() { return origenId; }
    public String getDestinoId() { return destinoId; }
    public double getDistancia() { return distancia; }   
    public double getTiempo() { return tiempo; }      
    public int getCapacidad() {
    return capacidad;
}
}