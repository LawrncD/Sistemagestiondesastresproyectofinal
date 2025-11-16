package co.edu.uniquindio.poo.app;
import co.edu.uniquindio.poo.model.*;
import co.edu.uniquindio.poo.ds.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Sistema de Gestión de Desastres Naturales ===");

        SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();

        boolean loginOk = sistema.login("admin@local", "admin123");
        System.out.println("Login admin@local → " + (loginOk ? "Éxito ✅" : "Error ❌"));

        // PRUEBA DE RUTA MÁS CORTA

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
        
        // === ÁRBOL DE DISTRIBUCIÓN ===
        System.out.println("\n=== Árbol de Distribución de Recursos ===");
        
        ArbolDistribucion arbol = new ArbolDistribucion();
        
        // Crear zonas para el árbol
        ZonaAfectada centroDistribucion = new ZonaAfectada("Centro Logístico Principal", 500, 30, 4.5380, -75.6680);
        ZonaAfectada zona1 = new ZonaAfectada("Zona Norte - Alta Montaña", 1200, 85, 4.6000, -75.7000);
        ZonaAfectada zona2 = new ZonaAfectada("Zona Sur - Valle", 800, 65, 4.4800, -75.6500);
        ZonaAfectada zona3 = new ZonaAfectada("Zona Este - Rural", 1500, 90, 4.5500, -75.6000);
        ZonaAfectada zona4 = new ZonaAfectada("Zona Oeste - Urbana", 2000, 75, 4.5200, -75.7200);
        ZonaAfectada zona5 = new ZonaAfectada("Zona Centro - Refugio", 600, 50, 4.5400, -75.6700);
        
        // Establecer centro de distribución con recursos disponibles
        Map<TipoRecurso, Integer> recursosDisponibles = new HashMap<>();
        recursosDisponibles.put(TipoRecurso.AGUA, 5000);
        recursosDisponibles.put(TipoRecurso.ALIMENTO, 3000);
        recursosDisponibles.put(TipoRecurso.MEDICINA, 1000);
        recursosDisponibles.put(TipoRecurso.EQUIPO, 500);
        
        arbol.establecerCentroDistribucion(centroDistribucion, recursosDisponibles);
        
        // Agregar zonas dependientes con sus necesidades
        Map<TipoRecurso, Integer> necesidadesZona1 = new HashMap<>();
        necesidadesZona1.put(TipoRecurso.AGUA, 800);
        necesidadesZona1.put(TipoRecurso.MEDICINA, 200);
        necesidadesZona1.put(TipoRecurso.EQUIPO, 100);
        arbol.agregarZonaDependiente(centroDistribucion.getId(), zona1, necesidadesZona1);
        
        Map<TipoRecurso, Integer> necesidadesZona2 = new HashMap<>();
        necesidadesZona2.put(TipoRecurso.ALIMENTO, 600);
        necesidadesZona2.put(TipoRecurso.AGUA, 500);
        arbol.agregarZonaDependiente(centroDistribucion.getId(), zona2, necesidadesZona2);
        
        Map<TipoRecurso, Integer> necesidadesZona3 = new HashMap<>();
        necesidadesZona3.put(TipoRecurso.AGUA, 1000);
        necesidadesZona3.put(TipoRecurso.ALIMENTO, 800);
        necesidadesZona3.put(TipoRecurso.MEDICINA, 300);
        arbol.agregarZonaDependiente(centroDistribucion.getId(), zona3, necesidadesZona3);
        
        Map<TipoRecurso, Integer> necesidadesZona4 = new HashMap<>();
        necesidadesZona4.put(TipoRecurso.ALIMENTO, 1200);
        necesidadesZona4.put(TipoRecurso.MEDICINA, 250);
        necesidadesZona4.put(TipoRecurso.EQUIPO, 200);
        arbol.agregarZonaDependiente(centroDistribucion.getId(), zona4, necesidadesZona4);
        
        // Agregar zona5 como dependiente de zona1 (sub-distribución)
        Map<TipoRecurso, Integer> necesidadesZona5 = new HashMap<>();
        necesidadesZona5.put(TipoRecurso.AGUA, 400);
        necesidadesZona5.put(TipoRecurso.ALIMENTO, 300);
        arbol.agregarZonaDependiente(zona1.getId(), zona5, necesidadesZona5);
        
        // Mostrar el árbol completo
        arbol.mostrarArbol();
        
        // Generar plan de distribución
        System.out.println("\n");
        List<String> plan = arbol.generarPlanDistribucion();
        for (String linea : plan) {
            System.out.println(linea);
        }
        
        // Calcular prioridades individuales
        System.out.println("\n📊 PRIORIDADES DE DISTRIBUCIÓN:");
        System.out.println("═══════════════════════════════════════");
        System.out.println("  • " + zona1.getNombre() + ": " + arbol.calcularPrioridadDistribucion(zona1.getId()));
        System.out.println("  • " + zona2.getNombre() + ": " + arbol.calcularPrioridadDistribucion(zona2.getId()));
        System.out.println("  • " + zona3.getNombre() + ": " + arbol.calcularPrioridadDistribucion(zona3.getId()));
        System.out.println("  • " + zona4.getNombre() + ": " + arbol.calcularPrioridadDistribucion(zona4.getId()));
        System.out.println("  • " + zona5.getNombre() + ": " + arbol.calcularPrioridadDistribucion(zona5.getId()));
        
        // Optimizar distribución por proximidad
        System.out.println("\n🗺️ DISTRIBUCIÓN OPTIMIZADA POR PROXIMIDAD:");
        Map<String, Map<TipoRecurso, Integer>> distribucion = arbol.optimizarDistribucionPorProximidad();
        for (Map.Entry<String, Map<TipoRecurso, Integer>> entry : distribucion.entrySet()) {
            System.out.println("  📍 Zona: " + entry.getKey());
            for (Map.Entry<TipoRecurso, Integer> recurso : entry.getValue().entrySet()) {
                System.out.println("      └─ " + recurso.getKey() + ": " + recurso.getValue() + " unidades");
            }
        }
        
        sistema.generarDashboardHTML();
    
    }
    
    
}