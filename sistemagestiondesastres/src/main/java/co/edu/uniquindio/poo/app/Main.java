package co.edu.uniquindio.poo.app;
import co.edu.uniquindio.poo.model.*;
import co.edu.uniquindio.poo.ds.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Sistema de Gestión de Desastres Naturales ===");

        SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();

        boolean loginOk = sistema.login("admin@local", "admin123");
        System.out.println("Login admin@local → " + (loginOk ? "Éxito ✅" : "Error ❌"));

        // 🚗 PRUEBA DE RUTA MÁS CORTA

        System.out.println("\n=== Prueba de Rutas ===");
        GrafoDirigido grafo = sistema.getGrafo();

        // Tomar las dos zonas creadas por defecto en cargarDatosIniciales()

        var zonas = grafo.obtenerZonas();
        String origen = null, destino = null;
        for (var z : zonas) {
            if (z.getNombre().equalsIgnoreCase("Ciudad A")) origen = z.getId();
            if (z.getNombre().equalsIgnoreCase("Refugio B")) destino = z.getId();
        }
        if (origen != null && destino != null) {
            grafo.imprimirRutaMasCorta(origen, destino);
       
        } else {
            System.out.println("❌ No se encontraron las zonas esperadas.");
        }

        //  Reporte final
        System.out.println("\n" + sistema.generarReporteEstadisticas().generarContenido());
        // === GESTIÓN DE RECURSOS ===
        System.out.println("\n=== Gestión de Recursos ===");
        Admin admin = (Admin) sistema.buscarUsuarioPorCorreo("admin@local");

        // Mostrar inventario inicial
        admin.mostrarInventario(sistema);

        // Asignar recursos desde el almacén central a Ciudad A
        admin.asignarRecursos(sistema, "Ciudad A", TipoRecurso.ALIMENTO, 200);
        admin.asignarRecursos(sistema, "Refugio B", TipoRecurso.MEDICINA, 50);

        // Mostrar inventario actualizado
        admin.mostrarInventario(sistema);
        // === GESTIÓN DE EVACUACIONES ===
        System.out.println("\n=== Gestión de Evacuaciones ===");
        // Tomar zonas del grafo
        zonas = sistema.getGrafo().obtenerZonas();
        for (var z : zonas) {
          sistema.getColaEvacuaciones().registrarZonaEvacuacion(z);
        }

        // Mostrar la cola inicial
        sistema.getColaEvacuaciones().mostrarCola();

        // Procesar dos evacuaciones
        sistema.getColaEvacuaciones().procesarEvacuacion();
        sistema.getColaEvacuaciones().procesarEvacuacion();

        // Mostrar la cola restante
        sistema.getColaEvacuaciones().mostrarCola();
        sistema.generarDashboardHTML();
    
    }
    
    
}