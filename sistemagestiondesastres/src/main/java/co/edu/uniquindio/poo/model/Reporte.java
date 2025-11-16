package co.edu.uniquindio.poo.model;

import java.util.Date;
import java.util.UUID;

public class Reporte {
    private String id;
    private Date fecha;
    private String contenido;
    private String usuarioGenerador;

    public Reporte(String contenido, String usuarioGenerador) {
        this.id = UUID.randomUUID().toString();
        this.fecha = new Date();
        this.contenido = contenido;
        this.usuarioGenerador = usuarioGenerador;
    }
    // metodos generales 

    public String generarContenido() {
        return String.format("Reporte (%s) - %s: %s", id, fecha.toString(), contenido);
    }

    public boolean esValido() {
        return contenido != null && !contenido.isEmpty();
    }
}
