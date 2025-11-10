package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/resources")
public class ApiResourcesServlet extends HttpServlet {
    private SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(sistema.getMapaRecursos().getRecursosPorUbicacion()));
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String,Object> body = gson.fromJson(req.getReader(), Map.class);
        String origen = (String) body.get("origen");
        String destino = (String) body.get("destino");
        String tipo = (String) body.get("tipo");
        Double cantidadD = (Double) body.get("cantidad");
        int cantidad = cantidadD.intValue();
        boolean ok = sistema.getMapaRecursos().transferirRecursos(origen, destino,
                      co.edu.uniquindio.poo.model.TipoRecurso.valueOf(tipo), cantidad);
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(Map.of("ok", ok)));
    }
}
