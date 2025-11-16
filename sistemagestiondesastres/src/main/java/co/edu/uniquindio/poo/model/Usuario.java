package co.edu.uniquindio.poo.model;

import java.util.Objects;

public abstract class Usuario {
    protected String rol;
    protected String id;
    protected String nombre;
    protected String passwd; 
    protected String numero;

    public Usuario(String id, String nombre, String passwd, String numero, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.passwd = passwd;
        this.numero = numero;
        this.rol = rol;
    }
/*
 * Verifica si la contraseña proporcionada coincide con la del usuario
 */
    public boolean verificarPassword(String password) {
        return Objects.equals(this.passwd, password);
    }

    public boolean cambiarPassword(String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.isEmpty()) return false;
        this.passwd = nuevaPassword;
        return true;
    }

    public boolean tienePermiso(String permiso) {
        // Implementación simple: permisos por rol (se puede mejorar)
        if ("ADMIN".equalsIgnoreCase(rol)) return true;
        if ("OPERADOR".equalsIgnoreCase(rol) && permiso.startsWith("OPER_")) return true;
        return false;
    }

    // getters y setters
    public String getRol() { return rol; }
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getNumero() { return numero; }
}
