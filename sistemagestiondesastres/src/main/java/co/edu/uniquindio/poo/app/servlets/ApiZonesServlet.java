package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.EquipoDeRescate;
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
        
        // CORS headers
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            var zonas = sistema.getGrafo().obtenerZonas();
            System.out.println("DEBUG ApiZonesServlet GET - Zonas encontradas: " + zonas.size());
            
            // Construir JSON manualmente para incluir equipos asignados
            StringBuilder jsonBuilder = new StringBuilder("[");
            boolean first = true;
            for (ZonaAfectada zona : zonas) {
                if (!first) jsonBuilder.append(",");
                first = false;
                
                jsonBuilder.append("{");
                jsonBuilder.append("\"id\":\"").append(zona.getId()).append("\",");
                jsonBuilder.append("\"nombre\":\"").append(zona.getNombre()).append("\",");
                jsonBuilder.append("\"poblacion\":").append(zona.getPoblacion()).append(",");
                jsonBuilder.append("\"poblacionInicial\":").append(zona.getPoblacionInicial()).append(",");
                jsonBuilder.append("\"evacuada\":").append(zona.isEvacuada()).append(",");
                jsonBuilder.append("\"nivelDeRiesgo\":").append(zona.getNivelDeRiesgo()).append(",");
                jsonBuilder.append("\"lat\":").append(zona.getLat()).append(",");
                jsonBuilder.append("\"lng\":").append(zona.getLng()).append(",");
                
                // Recursos asignados
                jsonBuilder.append("\"recursosAsignados\":{");
                var recursos = zona.getRecursosAsignados();
                boolean firstRecurso = true;
                for (var entry : recursos.entrySet()) {
                    if (!firstRecurso) jsonBuilder.append(",");
                    firstRecurso = false;
                    jsonBuilder.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
                }
                jsonBuilder.append("},");
                
                // Equipos asignados
                jsonBuilder.append("\"equiposAsignados\":[");
                List<EquipoDeRescate> equipos = zona.getEquiposAsignados();
                boolean firstEquipo = true;
                for (EquipoDeRescate equipo : equipos) {
                    if (!firstEquipo) jsonBuilder.append(",");
                    firstEquipo = false;
                    
                    jsonBuilder.append("{");
                    jsonBuilder.append("\"id\":\"").append(equipo.getId()).append("\",");
                    jsonBuilder.append("\"tipo\":\"").append(equipo.getTipo()).append("\",");
                    jsonBuilder.append("\"miembros\":").append(equipo.getMiembros()).append(",");
                    jsonBuilder.append("\"disponible\":").append(equipo.estaDisponible()).append(",");
                    
                    // Especialidades
                    jsonBuilder.append("\"especialidades\":[");
                    List<String> especialidades = equipo.getEspecialidades();
                    for (int i = 0; i < especialidades.size(); i++) {
                        if (i > 0) jsonBuilder.append(",");
                        jsonBuilder.append("\"").append(especialidades.get(i)).append("\"");
                    }
                    jsonBuilder.append("]");
                    jsonBuilder.append("}");
                }
                jsonBuilder.append("]");
                
                jsonBuilder.append("}");
            }
            jsonBuilder.append("]");
            
            String jsonResult = jsonBuilder.toString();
            System.out.println("DEBUG ApiZonesServlet GET - JSON: " + jsonResult);
            
            resp.getWriter().write(jsonResult);
        } catch (Exception e) {
            System.err.println("ERROR ApiZonesServlet GET: " + e.getMessage());
            e.printStackTrace();
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
        
        // CORS headers
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

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
            
            // Leer coordenadas (lat y lng)
            Double lat = jsonRequest.has("lat") ? jsonRequest.get("lat").getAsDouble() : null;
            Double lng = jsonRequest.has("lng") ? jsonRequest.get("lng").getAsDouble() : null;

            System.out.println("DEBUG ApiZonesServlet - Nombre=" + nombre + ", Poblacion=" + poblacion + ", Riesgo=" + nivelDeRiesgo + ", Lat=" + lat + ", Lng=" + lng);

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

            // Validar coordenadas
            if (lat == null || lng == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "Las coordenadas (lat y lng) son requeridas");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Validar que las coordenadas estén en Colombia (aproximadamente)
            if (lat < -4.0 || lat > 13.0 || lng < -80.0 || lng > -66.0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "Las coordenadas deben estar dentro del territorio colombiano");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Crear la nueva zona CON coordenadas
            ZonaAfectada nuevaZona = new ZonaAfectada(nombre, poblacion, nivelDeRiesgo, lat, lng);
            System.out.println("DEBUG ApiZonesServlet - Zona creada con ID: " + nuevaZona.getId() + " en (" + lat + ", " + lng + ")");
            System.out.println("DEBUG ApiZonesServlet - Nivel de riesgo DESPUÉS de crear: " + nuevaZona.getNivelDeRiesgo());

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
            System.out.println("DEBUG ApiZonesServlet - Verificación DESPUÉS de agregar al grafo: " + nuevaZona.getNivelDeRiesgo());
            
            // Verificar que la zona en el grafo tiene el nivel de riesgo correcto
            ZonaAfectada zonaEnGrafo = sistema.getGrafo().obtenerZonaPorId(nuevaZona.getId());
            if (zonaEnGrafo != null) {
                System.out.println("DEBUG ApiZonesServlet - Nivel de riesgo de zona EN GRAFO: " + zonaEnGrafo.getNivelDeRiesgo());
            }

            // Respuesta exitosa
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonObject response = new JsonObject();
            response.addProperty("ok", true);
            response.addProperty("msg", "Zona creada exitosamente");
            
            // Serializar manualmente para debug
            String jsonZona = gson.toJson(nuevaZona);
            System.out.println("DEBUG ApiZonesServlet - JSON de respuesta: " + jsonZona);
            
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }

            JsonObject jsonRequest = gson.fromJson(sb.toString(), JsonObject.class);
            String id = jsonRequest.has("id") ? jsonRequest.get("id").getAsString() : null;

            if (id == null || id.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "El ID de la zona es requerido");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            ZonaAfectada zonaExistente = sistema.getGrafo().obtenerZonaPorId(id);
            if (zonaExistente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "Zona no encontrada");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            if (jsonRequest.has("nombre")) {
                zonaExistente.setNombre(jsonRequest.get("nombre").getAsString());
            }
            if (jsonRequest.has("poblacion")) {
                zonaExistente.setPoblacion(jsonRequest.get("poblacion").getAsInt());
            }
            if (jsonRequest.has("nivelDeRiesgo")) {
                zonaExistente.setNivelDeRiesgo(jsonRequest.get("nivelDeRiesgo").getAsInt());
            }
            if (jsonRequest.has("lat") && jsonRequest.has("lng")) {
                zonaExistente.setLat(jsonRequest.get("lat").getAsDouble());
                zonaExistente.setLng(jsonRequest.get("lng").getAsDouble());
            }

            JsonObject response = new JsonObject();
            response.addProperty("ok", true);
            response.addProperty("msg", "Zona actualizada exitosamente");
            response.add("data", gson.toJsonTree(zonaExistente));
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al actualizar zona: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}