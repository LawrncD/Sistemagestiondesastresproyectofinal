package co.edu.uniquindio.poo.app;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import co.edu.uniquindio.poo.ds.ArbolDistribucion;
import co.edu.uniquindio.poo.ds.ColaPrioridadEvacuaciones;
import co.edu.uniquindio.poo.ds.GrafoDirigido;
import co.edu.uniquindio.poo.ds.MapaRecursos;
import co.edu.uniquindio.poo.model.Admin;
import co.edu.uniquindio.poo.model.OperadorDeEmergencia;
import co.edu.uniquindio.poo.model.Reporte;
import co.edu.uniquindio.poo.model.Ruta;
import co.edu.uniquindio.poo.model.TipoRecurso;
import co.edu.uniquindio.poo.model.Usuario;
import co.edu.uniquindio.poo.model.ZonaAfectada;

public class SistemaGestionDesastres {
    private static SistemaGestionDesastres instance;

    private Map<String, Usuario> usuarios = new HashMap<>();
    private GrafoDirigido grafo = new GrafoDirigido();
    private MapaRecursos mapaRecursos = new MapaRecursos();
    private ArbolDistribucion arbolDistribucion = new ArbolDistribucion();
    private List<Reporte> reportes = new ArrayList<>();
    private ColaPrioridadEvacuaciones colaEvacuaciones = new ColaPrioridadEvacuaciones();

    public ColaPrioridadEvacuaciones getColaEvacuaciones() {
      return colaEvacuaciones;
    }

    private SistemaGestionDesastres() {
        cargarDatosIniciales();
    }

    public static synchronized SistemaGestionDesastres getInstance() {
        if (instance == null) instance = new SistemaGestionDesastres();
        return instance;
    }

    public boolean login(String email, String password) {
        // En este ejemplo usamos id como "email"
        Usuario u = usuarios.get(email);
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
        // Usuarios
    Admin admin = new Admin("admin@local", "Administrador", "admin123", "3001112222");
    registrarUsuario(admin);

    OperadorDeEmergencia op = new OperadorDeEmergencia("oper1@local", "Operador 1", "op123", "3003334444");
    registrarUsuario(op);

    // === ZONAS ===
    ZonaAfectada z1 = new ZonaAfectada("Ciudad A", 2000, 80);
    ZonaAfectada z2 = new ZonaAfectada("Refugio B", 300, 50);
    ZonaAfectada z3 = new ZonaAfectada("Centro de Ayuda C", 800, 60);

    grafo.agregarNodo(z1);
    grafo.agregarNodo(z2);
    grafo.agregarNodo(z3);

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
    // Inicializar las ubicaciones en el mapa de recursos
    mapaRecursos.agregarRecursosUbicacion("Ciudad A", new HashMap<>());
    mapaRecursos.agregarRecursosUbicacion("Refugio B", new HashMap<>());
    mapaRecursos.agregarRecursosUbicacion("Centro de Ayuda C", new HashMap<>());

    // Recursos iniciales
    Map<TipoRecurso, Integer> recs = new HashMap<>();
    recs.put(TipoRecurso.ALIMENTO, 1000);
    recs.put(TipoRecurso.MEDICINA, 200);
    mapaRecursos.agregarRecursosUbicacion("almacen-central", recs);
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
    public Usuario buscarUsuarioPorCorreo(String correo) {
    return usuarios.get(correo);
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
}