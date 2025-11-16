package co.edu.uniquindio.poo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una notificación del sistema de gestión de desastres.
 * 
 * Esta clase encapsula la información de eventos importantes que ocurren
 * en el sistema, permitiendo informar a los usuarios sobre evacuaciones
 * completadas, zonas en riesgo crítico, recursos bajos y asignación de equipos.
 * 
 * <p>Cada notificación tiene un identificador único, tipo, mensaje descriptivo,
 * marca de tiempo, estado de lectura y opcionalmente una zona relacionada.</p>
 * 
 * @author Sistema de Gestión de Desastres - Universidad del Quindío
 * @version 1.0
 * @since 2025
 */
public class Notificacion {
    /** Contador estático para generar identificadores únicos */
    private static int contadorId = 1;
    
    /** Identificador único de la notificación */
    private int id;
    
    /** Tipo de notificación que determina su categoría y prioridad */
    private TipoNotificacion tipo;
    
    /** Mensaje descriptivo del evento ocurrido */
    private String mensaje;
    
    /** Fecha y hora en que se generó la notificación */
    private LocalDateTime timestamp;
    
    /** Indica si la notificación ha sido leída por el usuario */
    private boolean leida;
    
    /** Identificador de la zona afectada relacionada con la notificación (opcional) */
    private String zonaRelacionada;

    /**
     * Crea una nueva notificación sin zona relacionada.
     * 
     * @param tipo Tipo de notificación que determina su clasificación
     * @param mensaje Descripción detallada del evento
     */
    public Notificacion(TipoNotificacion tipo, String mensaje) {
        this.id = contadorId++;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
        this.leida = false;
        this.zonaRelacionada = null;
    }

    /**
     * Crea una nueva notificación asociada a una zona específica.
     * 
     * @param tipo Tipo de notificación que determina su clasificación
     * @param mensaje Descripción detallada del evento
     * @param zonaRelacionada Identificador de la zona afectada
     */
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

    /**
     * Obtiene la marca de tiempo formateada en formato legible.
     * 
     * @return Fecha y hora en formato "dd/MM/yyyy HH:mm:ss"
     */
    public String getTimestampFormateado() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    /**
     * Verifica si la notificación ha sido leída.
     * 
     * @return true si fue leída, false en caso contrario
     */
    public boolean isLeida() {
        return leida;
    }

    /**
     * Marca esta notificación como leída por el usuario.
     * Este método se invoca cuando el usuario visualiza la notificación.
     */
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
     * Enumeración que define los tipos de notificaciones del sistema.
     * 
     * Cada tipo incluye una clase CSS para estilización visual y un icono
     * representativo que facilita la identificación rápida del evento.
     * 
     * <p>Los tipos disponibles son:</p>
     * <ul>
     *   <li>EVACUACION_COMPLETADA: Proceso de evacuación finalizado exitosamente</li>
     *   <li>ZONA_EVACUADA: Zona completamente evacuada sin población restante</li>
     *   <li>RIESGO_CRITICO: Nivel de riesgo alcanzó umbral crítico</li>
     *   <li>RECURSOS_BAJOS: Recursos disponibles por debajo del mínimo requerido</li>
     *   <li>EQUIPO_ASIGNADO: Equipo de rescate asignado a una zona</li>
     * </ul>
     */
    public enum TipoNotificacion {
        /** Notificación de evacuación completada exitosamente */
        EVACUACION_COMPLETADA("success", "✅"),
        
        /** Notificación de zona completamente evacuada */
        ZONA_EVACUADA("info", "🏘️"),
        
        /** Alerta de nivel de riesgo crítico alcanzado */
        RIESGO_CRITICO("danger", "⚠️"),
        
        /** Advertencia de recursos por debajo del nivel mínimo */
        RECURSOS_BAJOS("warning", "📦"),
        
        /** Confirmación de asignación de equipo de rescate */
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
