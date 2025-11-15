package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.EquipoDeRescate;
import co.edu.uniquindio.poo.model.Notificacion.TipoNotificacion;
import co.edu.uniquindio.poo.model.ZonaAfectada;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiEquiposServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        
        SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
        Map<String, EquipoDeRescate> equipos = sistema.getEquiposDisponibles();
        
        // Convertir a formato JSON con información detallada
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (EquipoDeRescate equipo : equipos.values()) {
            if (!first) json.append(",");
            first = false;
            
            json.append("{");
            json.append("\"id\":\"").append(equipo.getId()).append("\",");
            json.append("\"tipo\":\"").append(equipo.getTipo()).append("\",");
            json.append("\"miembros\":").append(equipo.getMiembros()).append(",");
            json.append("\"disponible\":").append(equipo.estaDisponible()).append(",");
            json.append("\"ubicacionActual\":").append(equipo.getUbicacionActual() != null ? 
                "\"" + equipo.getUbicacionActual() + "\"" : "null").append(",");
            
            json.append("\"especialidades\":[");
            List<String> especialidades = equipo.getEspecialidades();
            for (int i = 0; i < especialidades.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(especialidades.get(i)).append("\"");
            }
            json.append("]");
            json.append("}");
        }
        json.append("]");
        
        System.out.println("DEBUG ApiEquiposServlet GET - Equipos: " + equipos.size());
        resp.getWriter().write(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            // Leer el cuerpo de la petición
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            JsonObject json = gson.fromJson(sb.toString(), JsonObject.class);
            String equipoId = json.get("equipoId").getAsString();
            String zonaId = json.get("zonaId").getAsString();
            
            System.out.println("DEBUG ApiEquiposServlet POST - Asignar equipo " + equipoId + " a zona " + zonaId);
            
            SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
            boolean exito = sistema.asignarEquipoAZona(equipoId, zonaId);
            
            if (exito) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"success\":true,\"message\":\"Equipo asignado correctamente\"}");
                System.out.println("DEBUG ApiEquiposServlet POST - Equipo asignado exitosamente");
                
                // 🔔 NOTIFICACIÓN: Equipo asignado
                EquipoDeRescate equipo = sistema.getEquiposDisponibles().get(equipoId);
                ZonaAfectada zona = sistema.getGrafo().obtenerZonaPorId(zonaId);
                if (equipo != null && zona != null) {
                    sistema.agregarNotificacion(
                        TipoNotificacion.EQUIPO_ASIGNADO,
                        "Equipo " + equipo.getTipo() + " (" + equipoId + ") asignado a " + zona.getNombre(),
                        zonaId
                    );
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false,\"message\":\"No se pudo asignar el equipo\"}");
                System.out.println("DEBUG ApiEquiposServlet POST - Error al asignar equipo");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR ApiEquiposServlet POST: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            String equipoId = req.getParameter("equipoId");
            String zonaId = req.getParameter("zonaId");
            
            if (equipoId == null || zonaId == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false,\"message\":\"Faltan parámetros\"}");
                return;
            }
            
            System.out.println("DEBUG ApiEquiposServlet DELETE - Liberar equipo " + equipoId + " de zona " + zonaId);
            
            SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
            boolean exito = sistema.liberarEquipo(equipoId, zonaId);
            
            if (exito) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"success\":true,\"message\":\"Equipo liberado correctamente\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false,\"message\":\"No se pudo liberar el equipo\"}");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR ApiEquiposServlet DELETE: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}");
        }
    }
}
