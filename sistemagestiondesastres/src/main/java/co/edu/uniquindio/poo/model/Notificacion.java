package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una notificación del sistema de gestión de desastres
 */
public class Notificacion {
    private static int contadorId = 1;
    
    private int id;
    private TipoNotificacion tipo;
    private String mensaje;
    private LocalDateTime timestamp;
    private boolean leida;
    private String zonaRelacionada; // opcional, para vincular con zonas

    public Notificacion(TipoNotificacion tipo, String mensaje) {
        this.id = contadorId++;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
        this.leida = false;
        this.zonaRelacionada = null;
    }

    public Notificacion(TipoNotificacion tipo, String mensaje, String zonaRelacionada) {
        this(tipo, mensaje);
        this.zonaRelacionada = zonaRelacionada;
    }

    public int getId() {
        return id;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTimestampFormateado() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    public boolean isLeida() {
        return leida;
    }

    public void marcarComoLeida() {
        this.leida = true;
    }

    public String getZonaRelacionada() {
        return zonaRelacionada;
    }

    public void setZonaRelacionada(String zonaRelacionada) {
        this.zonaRelacionada = zonaRelacionada;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (Leída: %s)", 
            tipo, getTimestampFormateado(), mensaje, leida ? "Sí" : "No");
    }

    /**
     * Enum para los tipos de notificaciones
     */
    public enum TipoNotificacion {
        EVACUACION_COMPLETADA("success", "✅"),
        ZONA_EVACUADA("info", "🏘️"),
        RIESGO_CRITICO("danger", "⚠️"),
        RECURSOS_BAJOS("warning", "📦"),
        EQUIPO_ASIGNADO("success", "🚑");

        private final String clase; // clase CSS para colorear
        private final String icono;

        TipoNotificacion(String clase, String icono) {
            this.clase = clase;
            this.icono = icono;
        }

        public String getClase() {
            return clase;
        }

        public String getIcono() {
            return icono;
        }
    }
}
