package co.edu.uniquindio.poo.model;

public class OperadorDeEmergencia extends Usuario {

    public OperadorDeEmergencia(String id, String nombre, String passwd, String numero) {
        super(id, nombre, passwd, numero, "OPERADOR");
    }

    public boolean actualizarEstadoZona(String zonaId, String estado, String observaciones) {
        // Stub: en fase 2 implementaremos el enlace con ZonaAfectada
        return true;
    }

    public boolean reportarIncidente(String zonaId, String tipoIncidente, int urgencia) {
        // Stub
        return true;
    }
}