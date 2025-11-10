package co.edu.uniquindio.poo.app;

public class ResultadoSimulacion {
    private String mensaje;
    private boolean exito;

    public ResultadoSimulacion(String mensaje, boolean exito) {
        this.mensaje = mensaje;
        this.exito = exito;
    }

    public String getMensaje() { return mensaje; }
    public boolean isExito() { return exito; }
}