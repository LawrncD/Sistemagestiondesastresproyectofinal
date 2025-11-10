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

        // Servlets
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.LoginServlet.class, "/login");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.LogoutServlet.class, "/logout");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiZonesServlet.class, "/api/zones");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiRoutesServlet.class, "/api/routes");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiResourcesServlet.class, "/api/resources");
        ctx.addServlet(co.edu.uniquindio.poo.app.servlets.ApiEvacuacionesServlet.class, "/api/evacuations");

        server.setHandler(ctx);
        server.start();
        System.out.println("Server started at http://localhost:" + port + " (open login.html)");
        server.join();
    }
}
