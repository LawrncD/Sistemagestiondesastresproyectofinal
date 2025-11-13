package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.ZonaAfectada;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/zones")
public class ApiZonesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            var zonas = sistema.getGrafo().obtenerZonas();
            resp.getWriter().write(gson.toJson(zonas));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al obtener zonas: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Leer el cuerpo de la petición
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            System.out.println("DEBUG ApiZonesServlet - Recibido JSON: " + jsonString);

            // Parsear JSON
            JsonObject jsonRequest = gson.fromJson(jsonString, JsonObject.class);

            String nombre = jsonRequest.has("nombre") ? jsonRequest.get("nombre").getAsString() : null;
            int poblacion = jsonRequest.has("poblacion") ? jsonRequest.get("poblacion").getAsInt() : 0;
            int nivelDeRiesgo = jsonRequest.has("nivelDeRiesgo") ? jsonRequest.get("nivelDeRiesgo").getAsInt() : 0;

            System.out.println("DEBUG ApiZonesServlet - Nombre=" + nombre + ", Poblacion=" + poblacion + ", Riesgo=" + nivelDeRiesgo);

            // Validar datos
            if (nombre == null || nombre.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "El nombre es requerido");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            if (poblacion <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "La población debe ser mayor a 0");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            if (nivelDeRiesgo < 0 || nivelDeRiesgo > 100) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "El nivel de riesgo debe estar entre 0 y 100");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Crear la nueva zona
            ZonaAfectada nuevaZona = new ZonaAfectada(nombre, poblacion, nivelDeRiesgo);
            System.out.println("DEBUG ApiZonesServlet - Zona creada con ID: " + nuevaZona.getId());

            // Agregar al grafo
            boolean agregada = sistema.getGrafo().agregarZona(nuevaZona);

            if (!agregada) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "No se pudo agregar la zona (posible duplicado)");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            System.out.println("DEBUG ApiZonesServlet - Zona agregada exitosamente al grafo");

            // Respuesta exitosa
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject response = new JsonObject();
            response.addProperty("ok", true);
            response.addProperty("msg", "Zona creada exitosamente");
            response.add("data", gson.toJsonTree(nuevaZona));
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR ApiZonesServlet: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al crear zona: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }
}