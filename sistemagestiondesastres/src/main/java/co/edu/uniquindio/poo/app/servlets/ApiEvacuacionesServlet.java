package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.ZonaAfectada;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import co.edu.uniquindio.poo.model.ZonaAfectada;

@WebServlet("/api/evacuations")
public class ApiEvacuacionesServlet extends HttpServlet {
    private final SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Obtener lista de zonas ordenadas por nivel de riesgo
        List<ZonaAfectada> zonas = sistema.getGrafo().obtenerZonas();
        zonas.sort((a, b) -> Integer.compare(b.getNivelDeRiesgo(), a.getNivelDeRiesgo()));

        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(zonas));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> body = gson.fromJson(req.getReader(), Map.class);
        String zonaId = (String) body.get("zonaId");

        Number nPersonas = (Number) body.get("personas");
        int personas = nPersonas == null ? 0 : nPersonas.intValue();

        // Buscar la zona afectada en el grafo
        var zona = sistema.getGrafo().obtenerZonaPorId(zonaId);
        if (zona == null) {
            resp.getWriter().write(gson.toJson(Map.of("ok", false, "msg", "Zona no encontrada")));
            return;
        }

        // Registrar la zona en la cola de evacuación
        sistema.getColaEvacuaciones().registrarZonaEvacuacion(zona);

        // Enviar respuesta
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(gson.toJson(Map.of("ok", true, "zona", zona.getNombre())));
    }
}


