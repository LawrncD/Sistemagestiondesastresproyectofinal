package co.edu.uniquindio.poo.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.uniquindio.poo.ds.ArbolDistribucion;
import co.edu.uniquindio.poo.ds.ColaPrioridadEvacuaciones;
import co.edu.uniquindio.poo.ds.GrafoDirigido;
import co.edu.uniquindio.poo.ds.MapaRecursos;
import co.edu.uniquindio.poo.model.Admin;
import co.edu.uniquindio.poo.model.EquipoDeRescate;
import co.edu.uniquindio.poo.model.Notificacion;
import co.edu.uniquindio.poo.model.Notificacion.TipoNotificacion;
import co.edu.uniquindio.poo.model.OperadorDeEmergencia;
import co.edu.uniquindio.poo.model.Reporte;
import co.edu.uniquindio.poo.model.Ruta;
import co.edu.uniquindio.poo.model.TipoEquipo;
import co.edu.uniquindio.poo.model.TipoRecurso;
import co.edu.uniquindio.poo.model.Usuario;
import co.edu.uniquindio.poo.model.ZonaAfectada;

/**
 * Clase principal del sistema de gestión de desastres que implementa el patrón Singleton.
 * 
 * Esta clase centraliza toda la lógica de negocio del sistema, gestionando usuarios,
 * zonas afectadas, recursos, equipos de rescate, evacuaciones y notificaciones.
 * 
 * <p>Responsabilidades principales:</p>
 * <ul>
 *   <li>Administración de usuarios (registro, autenticación)</li>
 *   <li>Gestión de zonas afectadas mediante grafo dirigido</li>
 *   <li>Control de recursos y su distribución</li>
 *   <li>Coordinación de equipos de rescate</li>
 *   <li>Priorización de evacuaciones</li>
 *   <li>Sistema de notificaciones en tiempo real</li>
 *   <li>Generación de reportes estadísticos</li>
 * </ul>
 * 
 * @author Sistema de Gestión de Desastres - Universidad del Quindío
 * @version 1.0
 * @since 2025
 */
public class SistemaGestionDesastres {
    /** Instancia única del sistema (patrón Singleton) */
    private static SistemaGestionDesastres instance;

    /** Mapa de usuarios registrados, indexados por su identificador */
    private Map<String, Usuario> usuarios = new HashMap<>();
    
    /** Grafo dirigido que representa las zonas afectadas y sus conexiones */
    private GrafoDirigido grafo = new GrafoDirigido();
    
    /** Mapa de recursos disponibles en diferentes ubicaciones */
    private MapaRecursos mapaRecursos = new MapaRecursos();
    
    /** Árbol de distribución para optimizar el envío de recursos */
    private ArbolDistribucion arbolDistribucion = new ArbolDistribucion();
    
    /** Lista de reportes generados por el sistema */
    private List<Reporte> reportes = new ArrayList<>();
    
    /** Cola de prioridad para gestionar evacuaciones según nivel de riesgo */
    private ColaPrioridadEvacuaciones colaEvacuaciones = new ColaPrioridadEvacuaciones();
    
    /** Mapa de equipos de rescate disponibles, indexados por su identificador */
    private Map<String, EquipoDeRescate> equiposDisponibles = new HashMap<>();
    
    /** Lista de notificaciones del sistema ordenada por recencia */
    private List<Notificacion> notificaciones = new ArrayList<>();

    public ColaPrioridadEvacuaciones getColaEvacuaciones() {
      return colaEvacuaciones;
    }

    public ArbolDistribucion getArbolDistribucion() {
      return arbolDistribucion;
    }

    private SistemaGestionDesastres() {
        cargarDatosIniciales();
    }

    public static synchronized SistemaGestionDesastres getInstance() {
        if (instance == null) instance = new SistemaGestionDesastres();
        return instance;
    }

    public boolean login(String email, String password) {
        // Convertir email a formato de ID (mismo formato que RegisterServlet)
        String userId = email.toLowerCase().replace("@", "_").replace(".", "_");
        Usuario u = usuarios.get(userId);
        if (u == null) return false;
        return u.verificarPassword(password);
    }

    public void logout() {
        // Si tuviéramos sesiones manejaríamos limpieza aquí
    }

    public boolean registrarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getId())) return false;
        usuarios.put(usuario.getId(), usuario);
        return true;
    }

    public void cargarDatosIniciales() {
        // Usuarios (IDs convertidos al formato usado en login: email con @ y . reemplazados por _)
    Admin admin = new Admin("admin_local", "Administrador", "admin123", "3001112222");
    registrarUsuario(admin);

    OperadorDeEmergencia op = new OperadorDeEmergencia("oper1_local", "Operador 1", "op123", "3003334444");
    registrarUsuario(op);

    // === ZONAS CON COORDENADAS REALES DE COLOMBIA ===
    // Bogotá (alta población, riesgo moderado)
    ZonaAfectada z1 = new ZonaAfectada("Bogotá Centro", 2000, 80, 4.7110, -74.0721);
    
    // Medellín (refugio)
    ZonaAfectada z2 = new ZonaAfectada("Refugio Medellín", 300, 50, 6.2442, -75.5812);
    
    // Cali (centro de ayuda)
    ZonaAfectada z3 = new ZonaAfectada("Centro Ayuda Cali", 800, 60, 3.4516, -76.5320);
    
    // Centro Armenia (zona de riesgo medio)
    ZonaAfectada z4 = new ZonaAfectada("Centro Armenia", 1200, 70, 4.5339, -75.6811);
    
    // La Tebaida (zona de apoyo)
    ZonaAfectada z5 = new ZonaAfectada("La Tebaida", 600, 55, 4.4563, -75.7847);

    grafo.agregarNodo(z1);
    grafo.agregarNodo(z2);
    grafo.agregarNodo(z3);
    grafo.agregarNodo(z4);
    grafo.agregarNodo(z5);

    // === RUTAS ===
    // A → B directa
    Ruta r1 = new Ruta(z1.getId(), z2.getId(), 10.0, 30.0, 100);
    grafo.agregarArista(r1);

    // A → C
    Ruta r2 = new Ruta(z1.getId(), z3.getId(), 5.0, 10.0, 100);
    grafo.agregarArista(r2);

    // C → B
    Ruta r3 = new Ruta(z3.getId(), z2.getId(), 5.0, 10.0, 100);
    grafo.agregarArista(r3);
    
    // Centro Armenia → La Tebaida
    Ruta r4 = new Ruta(z4.getId(), z5.getId(), 2.5, 5.0, 80);
    grafo.agregarArista(r4);
    
    // Cali → Centro Armenia
    Ruta r5 = new Ruta(z3.getId(), z4.getId(), 8.0, 20.0, 90);
    grafo.agregarArista(r5);
    
    // Inicializar las ubicaciones en el mapa de recursos
    mapaRecursos.agregarRecursosUbicacion("Bogotá Centro", new HashMap<>());
    mapaRecursos.agregarRecursosUbicacion("Refugio Medellín", new HashMap<>());
    mapaRecursos.agregarRecursosUbicacion("Centro Ayuda Cali", new HashMap<>());
    mapaRecursos.agregarRecursosUbicacion("Centro Armenia", new HashMap<>());
    mapaRecursos.agregarRecursosUbicacion("La Tebaida", new HashMap<>());

    // Recursos iniciales
    Map<TipoRecurso, Integer> recs = new HashMap<>();
    recs.put(TipoRecurso.ALIMENTO, 1000);
    recs.put(TipoRecurso.MEDICINA, 200);
    mapaRecursos.agregarRecursosUbicacion("almacen-central", recs);
    
    // Centro de distribución regional Armenia
    Map<TipoRecurso, Integer> recsArmenia = new HashMap<>();
    recsArmenia.put(TipoRecurso.ALIMENTO, 500);
    recsArmenia.put(TipoRecurso.AGUA, 800);
    recsArmenia.put(TipoRecurso.MEDICINA, 150);
    mapaRecursos.agregarRecursosUbicacion("centro-armenia", recsArmenia);

    // === EQUIPOS DE RESCATE INICIALES ===
    EquipoDeRescate equipo1 = new EquipoDeRescate(TipoEquipo.MEDICO, 8, null, List.of("Trauma", "Emergencias"));
    EquipoDeRescate equipo2 = new EquipoDeRescate(TipoEquipo.BOMBERO, 12, null, List.of("Rescate", "Incendios"));
    EquipoDeRescate equipo3 = new EquipoDeRescate(TipoEquipo.POLICIA, 10, null, List.of("Seguridad", "Evacuación"));
    EquipoDeRescate equipo4 = new EquipoDeRescate(TipoEquipo.VOLUNTARIO, 15, null, List.of("Logística", "Distribución"));
    EquipoDeRescate equipo5 = new EquipoDeRescate(TipoEquipo.MEDICO, 5, null, List.of("Pediatría", "Primeros Auxilios"));
    
    equiposDisponibles.put(equipo1.getId(), equipo1);
    equiposDisponibles.put(equipo2.getId(), equipo2);
    equiposDisponibles.put(equipo3.getId(), equipo3);
    equiposDisponibles.put(equipo4.getId(), equipo4);
    equiposDisponibles.put(equipo5.getId(), equipo5);
}


    public ResultadoSimulacion ejecutarSimulacion() {
        // Stub: devolver un resultado simplificado
        return new ResultadoSimulacion("Simulación completa (stub)", true);
    }

    public Reporte generarReporteEstadisticas() {
        Reporte rep = new Reporte("Reporte estadístico (stub)", "system");
        reportes.add(rep);
        return rep;
    }


    public void visualizarMapaInteractivo() {
        // En fase GUI esto se ignorará; GUI hará render del grafo
    }

    // getters para componentes importantes
    public GrafoDirigido getGrafo() { return grafo; }
    public MapaRecursos getMapaRecursos() { return mapaRecursos; }
    public Map<String, Usuario> getUsuarios() { return usuarios; }
    public Map<String, EquipoDeRescate> getEquiposDisponibles() { return equiposDisponibles; }
    
    public Usuario buscarUsuarioPorCorreo(String correo) {
        return usuarios.get(correo);
    }

    // Métodos para gestionar equipos
    public boolean asignarEquipoAZona(String equipoId, String zonaId) {
        EquipoDeRescate equipo = equiposDisponibles.get(equipoId);
        if (equipo == null || !equipo.estaDisponible()) {
            return false;
        }
        
        ZonaAfectada zona = grafo.obtenerZonaPorId(zonaId);
        if (zona == null) {
            return false;
        }
        
        equipo.asignarAZona(zonaId);
        zona.agregarEquipo(equipo);
        return true;
    }

    public boolean liberarEquipo(String equipoId, String zonaId) {
        EquipoDeRescate equipo = equiposDisponibles.get(equipoId);
        if (equipo == null) {
            return false;
        }
        
        ZonaAfectada zona = grafo.obtenerZonaPorId(zonaId);
        if (zona != null) {
            zona.removerEquipo(equipoId);
        }
        
        equipo.liberar();
        return true;
    }
public void generarDashboardHTML() {
    StringBuilder html = new StringBuilder();
    html.append("""
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <title>Dashboard - Sistema de Gestión de Desastres</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; background: #f4f6f9; }
                h1 { color: #004aad; text-align: center; }
                h2 { color: #333; margin-top: 30px; }
                table { width: 100%; border-collapse: collapse; margin-top: 10px; background: white; }
                th, td { padding: 10px; border: 1px solid #ccc; text-align: center; }
                th { background-color: #004aad; color: white; }
                tr.alto { background-color: #ffb3b3; }   /* Riesgo alto */
                tr.medio { background-color: #fff5ba; }  /* Riesgo medio */
                tr.bajo { background-color: #c6f5c6; }   /* Riesgo bajo */
                footer { text-align: center; margin-top: 40px; color: #555; }
            </style>
        </head>
        <body>
            <h1>🌎 Sistema de Gestión de Desastres Naturales</h1>
            <h2>Zonas Afectadas</h2>
            <table>
                <tr><th>Zona</th><th>Población</th><th>Riesgo</th></tr>
        """);

    // Zonas
    for (var z : grafo.obtenerZonas()) {
        String clase = z.getNivelDeRiesgo() >= 70 ? "alto"
                     : z.getNivelDeRiesgo() >= 50 ? "medio"
                     : "bajo";
        html.append("<tr class='" + clase + "'>")
            .append("<td>").append(z.getNombre()).append("</td>")
            .append("<td>").append(z.getPoblacion()).append("</td>")
            .append("<td>").append(z.getNivelDeRiesgo()).append("</td>")
            .append("</tr>");
    }

    html.append("""
        </table>
        <h2>Recursos por Ubicación</h2>
        <table>
            <tr><th>Ubicación</th><th>Recursos</th></tr>
        """);

    // Recursos
    for (var entry : mapaRecursos.getRecursosPorUbicacion().entrySet()) {
        html.append("<tr><td>").append(entry.getKey()).append("</td><td>")
            .append(entry.getValue().toString())
            .append("</td></tr>");
    }

    html.append("""
        </table>
        <footer>
            <p>Proyecto académico - Universidad del Quindío - 2025</p>
        </footer>
        </body>
        </html>
        """);

    try {
        java.nio.file.Files.writeString(java.nio.file.Path.of("dashboard.html"), html.toString());
        System.out.println("✅ Dashboard visual generado correctamente: dashboard.html");
    } catch (java.io.IOException e) {
        e.printStackTrace();
    }
}
public void generarInterfazCompletaHTML() {
    StringBuilder html = new StringBuilder();
    html.append("""
        <!DOCTYPE html>
        <html lang='es'>
        <head>
            <meta charset='UTF-8'>
            <title>Sistema de Gestión de Desastres</title>
            <style>
                body { font-family: Arial, sans-serif; background: #f2f4f8; margin: 0; padding: 0; }
                header { background: #004aad; color: white; padding: 1em; text-align: center; }
                nav button { margin: 0.3em; padding: 0.5em 1em; border: none; border-radius: 5px;
                             background: #007bff; color: white; cursor: pointer; }
                nav button:hover { background: #0056b3; }
                section { display: none; padding: 1em 2em; }
                section.activo { display: block; background: white; margin: 1em; border-radius: 10px; box-shadow: 0 0 10px #ccc; }
                table { width: 100%; border-collapse: collapse; margin-top: 10px; }
                th, td { padding: 8px; border: 1px solid #ddd; text-align: center; }
                th { background-color: #004aad; color: white; }
                tr.alto { background-color: #ffb3b3; }
                tr.medio { background-color: #fff5ba; }
                tr.bajo { background-color: #c6f5c6; }
            </style>
        </head>
        <body>
            <header>
                <h1>🌎 Sistema de Gestión de Desastres Naturales</h1>
                <nav>
                    <button onclick="mostrar('inicio')">Inicio</button>
                    <button onclick="mostrar('admin')">Administración</button>
                    <button onclick="mostrar('rutas')">Rutas</button>
                    <button onclick="mostrar('evacuaciones')">Evacuaciones</button>
                    <button onclick="mostrar('estadisticas')">Estadísticas</button>
                </nav>
            </header>
            <main>
    """);

    // 🟢 Panel INICIO
    html.append("""
        <section id='inicio' class='activo'>
            <h2>Resumen General</h2>
            <table>
                <tr><th>Zona</th><th>Población</th><th>Riesgo</th></tr>
    """);
    for (var z : grafo.obtenerZonas()) {
        String clase = z.getNivelDeRiesgo() >= 70 ? "alto"
                     : z.getNivelDeRiesgo() >= 50 ? "medio" : "bajo";
        html.append("<tr class='" + clase + "'><td>" + z.getNombre() + "</td><td>" +
                    z.getPoblacion() + "</td><td>" + z.getNivelDeRiesgo() + "</td></tr>");
    }
    html.append("</table></section>");

    // 🟢 Panel ADMINISTRACIÓN (recursos)
    html.append("<section id='admin'><h2>Gestión de Recursos</h2><table><tr><th>Ubicación</th><th>Recursos</th></tr>");
    for (var entry : mapaRecursos.getRecursosPorUbicacion().entrySet()) {
        html.append("<tr><td>").append(entry.getKey()).append("</td><td>")
            .append(entry.getValue().toString()).append("</td></tr>");
    }
    html.append("</table></section>");

    // 🟢 Panel RUTAS
    html.append("<section id='rutas'><h2>Rutas registradas</h2><table><tr><th>Origen</th><th>Destino</th><th>Distancia</th><th>Tiempo</th></tr>");
    for (List<Ruta> lista : grafo.getAristas().values()) {
    for (Ruta r : lista) {
        html.append("<tr><td>").append(r.getOrigenId()).append("</td><td>")
            .append(r.getDestinoId()).append("</td><td>")
            .append(r.getDistancia()).append(" km</td><td>")
            .append(r.getTiempo()).append(" min</td></tr>");
    }
}
    html.append("</table></section>");

    // 🟢 Panel EVACUACIONES
    html.append("<section id='evacuaciones'><h2>Cola de Evacuaciones</h2><ul>");
    colaEvacuaciones.mostrarCola(); // mostramos en consola también
    for (var z : grafo.obtenerZonas()) {
        html.append("<li>").append(z.getNombre())
            .append(" (Riesgo: ").append(z.getNivelDeRiesgo()).append(")</li>");
    }
    html.append("</ul></section>");

    // 🟢 Panel ESTADÍSTICAS
    html.append("""
        <section id='estadisticas'>
            <h2>Reportes y estadísticas</h2>
            <p>Último reporte generado: </p>
    """);
    html.append("<p>").append(generarReporteEstadisticas().generarContenido()).append("</p>");
    html.append("</section>");


    // Cierre
    html.append("""
            </main>
            <footer style='text-align:center; margin:2em;'>Proyecto Uniquindío 2025</footer>
            <script>
                function mostrar(id){
                    document.querySelectorAll('section').forEach(s => s.classList.remove('activo'));
                    document.getElementById(id).classList.add('activo');
                }
            </script>
        </body></html>
    """);

    try {
        java.nio.file.Files.writeString(java.nio.file.Path.of("interfaz.html"), html.toString());
        System.out.println("✅ Interfaz completa generada: interfaz.html");
    } catch (java.io.IOException e) {
        e.printStackTrace();
    }
}

    // ==================== MÉTODOS PARA NOTIFICACIONES ====================

    /**
     * Agrega una nueva notificación al sistema sin zona relacionada.
     * 
     * La notificación se agrega al inicio de la lista para mantener
     * el orden cronológico inverso (más recientes primero).
     * 
     * @param tipo Tipo de notificación a crear
     * @param mensaje Descripción detallada del evento
     */
    public void agregarNotificacion(TipoNotificacion tipo, String mensaje) {
        Notificacion notif = new Notificacion(tipo, mensaje);
        notificaciones.add(0, notif);
        System.out.println("Nueva notificación: " + mensaje);
    }

    /**
     * Agrega una nueva notificación asociada a una zona específica.
     * 
     * Este método vincula la notificación con una zona afectada,
     * permitiendo filtrar y consultar notificaciones por zona.
     * 
     * @param tipo Tipo de notificación a crear
     * @param mensaje Descripción detallada del evento
     * @param zonaId Identificador de la zona relacionada
     */
    public void agregarNotificacion(TipoNotificacion tipo, String mensaje, String zonaId) {
        Notificacion notif = new Notificacion(tipo, mensaje, zonaId);
        notificaciones.add(0, notif);
        System.out.println("Nueva notificación [" + zonaId + "]: " + mensaje);
    }

    /**
     * Obtiene una copia de todas las notificaciones del sistema.
     * 
     * Las notificaciones se retornan en orden cronológico inverso,
     * con las más recientes al inicio de la lista.
     * 
     * @return Lista inmutable de notificaciones
     */
    public List<Notificacion> obtenerNotificaciones() {
        return new ArrayList<>(notificaciones);
    }

    /**
     * Cuenta el número de notificaciones no leídas.
     * 
     * Este método es útil para mostrar badges de notificaciones
     * pendientes en la interfaz de usuario.
     * 
     * @return Cantidad de notificaciones sin leer
     */
    public int contarNotificacionesNoLeidas() {
        return (int) notificaciones.stream().filter(n -> !n.isLeida()).count();
    }

    /**
     * Marca una notificación específica como leída.
     * 
     * @param notifId Identificador de la notificación
     * @return true si la notificación fue encontrada y marcada, false en caso contrario
     */
    public boolean marcarNotificacionComoLeida(int notifId) {
        for (Notificacion n : notificaciones) {
            if (n.getId() == notifId) {
                n.marcarComoLeida();
                return true;
            }
        }
        return false;
    }

    /**
     * Marca todas las notificaciones del sistema como leídas.
     * 
     * Este método es útil cuando el usuario desea limpiar
     * todas las notificaciones pendientes de una vez.
     */
    public void marcarTodasNotificacionesComoLeidas() {
        for (Notificacion n : notificaciones) {
            n.marcarComoLeida();
        }
    }

    /**
     * Limpia notificaciones antiguas para mantener la lista manejable.
     * 
     * Conserva únicamente las notificaciones más recientes hasta
     * el límite especificado, eliminando las más antiguas.
     * 
     * @param maxNotificaciones Número máximo de notificaciones a conservar
     */
    public void limpiarNotificacionesAntiguas(int maxNotificaciones) {
        if (notificaciones.size() > maxNotificaciones) {
            notificaciones = new ArrayList<>(notificaciones.subList(0, maxNotificaciones));
        }
    }
}