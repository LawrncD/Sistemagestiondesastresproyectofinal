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
        resp.setContentType("application/json;charset=UTF-8");
        var zonas = sistema.getGrafo().obtenerZonas();
        zonas.sort((a, b) -> Integer.compare(b.getNivelDeRiesgo(), a.getNivelDeRiesgo()));
        resp.getWriter().write(gson.toJson(zonas));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            Map<String, Object> body = gson.fromJson(req.getReader(), Map.class);
            String zonaId = (String) body.get("zonaId");
            int personas = ((Double) body.get("personas")).intValue();

            var zona = sistema.getGrafo().obtenerZonaPorId(zonaId);
            if (zona == null)
                throw new IllegalArgumentException("Zona no encontrada");

            sistema.getColaEvacuaciones().registrarZonaEvacuacion(zona);
            resp.getWriter().write(gson.toJson(Map.of("ok", true, "msg", "Evacuación registrada", "zona", zona.getNombre())));
        } catch (Exception e) {
            resp.getWriter().write(gson.toJson(Map.of("ok", false, "msg", e.getMessage())));
        } finally {
            resp.flushBuffer();
        }
    }
}



