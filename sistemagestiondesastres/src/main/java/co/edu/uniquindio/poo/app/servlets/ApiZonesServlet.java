package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;

import com.google.gson.Gson;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/zones")
public class ApiZonesServlet extends HttpServlet {
    private SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        var zonas = sistema.getGrafo().obtenerZonas(); // List<ZonaAfectada>
        resp.getWriter().write(gson.toJson(zonas));
    }
}
