package co.edu.uniquindio.poo.app.servlets;

import com.google.gson.Gson;
import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.Ruta;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/routes")
public class ApiRoutesServlet extends HttpServlet {
    private SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        var aristas = sistema.getGrafo().getAristas(); // Map<String, List<Ruta>>
        // aplanar
        List<Ruta> lista = new ArrayList<>();
        for (var l : aristas.values()) lista.addAll(l);
        resp.getWriter().write(gson.toJson(lista));
    }
}
