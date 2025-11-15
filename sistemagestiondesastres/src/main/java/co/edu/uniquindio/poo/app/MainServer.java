package co.edu.uniquindio.poo.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import java.nio.file.Paths;

public class MainServer {
    public static void main(String[] args) throws Exception {
        // Puerto
        int port = 8080;
        Server server = new Server(port);

        // Contexto
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");
        
        // Static files - sirve src/main/resources/web como /
        String webdir = Paths.get("src/main/resources/web").toAbsolutePath().toString();
        System.out.println("Serving static files from: " + webdir);
        ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
        staticHolder.setInitParameter("resourceBase", webdir);
        staticHolder.setInitParameter("dirAllowed", "true");
        ctx.addServlet(staticHolder, "/");

        // Servlets de API
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.LoginServlet.class, "/login");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.RegisterServlet.class, "/register");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.LogoutServlet.class, "/logout");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiSessionServlet.class, "/api/session");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiZonesServlet.class, "/api/zones");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiRoutesServlet.class, "/api/routes");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiResourcesServlet.class, "/api/resources");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiEvacuacionesServlet.class, "/api/evacuations");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiOptimalRouteServlet.class, "/api/optimal-route");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiUsuariosServlet.class, "/api/users");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiEquiposServlet.class, "/api/equipos");

        server.setHandler(ctx);
        server.start();
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║     🌍 SISTEMA DE GESTIÓN DE DESASTRES - INICIADO 🌍     ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\n✅ Servidor activo en: http://localhost:" + port);
        System.out.println("📱 Abrir aplicación: http://localhost:" + port + "\n");
        System.out.println("🔐 CREDENCIALES DE PRUEBA:");
        System.out.println("   Admin:    admin@local / admin123");
        System.out.println("   Operador: oper1@local / op123");
        System.out.println("\n════════════════════════════════════════════════════════════\n");
        server.join();
    }
}
