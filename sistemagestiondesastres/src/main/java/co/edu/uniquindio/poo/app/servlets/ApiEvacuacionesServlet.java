package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.Notificacion.TipoNotificacion;
import co.edu.uniquindio.poo.model.ZonaAfectada;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/evacuations")
public class ApiEvacuacionesServlet extends HttpServlet {
    private final SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private final Gson gson = new Gson();
    
    // Lista temporal para almacenar evacuaciones registradas
    private static List<EvacuacionInfo> evacuacionesRegistradas = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        
        System.out.println("DEBUG ApiEvacuacionesServlet GET - Obteniendo evacuaciones...");
        
        try {
            // Devolver evacuaciones registradas ordenadas por prioridad (riesgo descendente)
            evacuacionesRegistradas.sort((e1, e2) -> Integer.compare(e2.prioridad, e1.prioridad));
            
            String json = gson.toJson(evacuacionesRegistradas);
            System.out.println("DEBUG ApiEvacuacionesServlet GET - Evacuaciones: " + evacuacionesRegistradas.size());
            
            resp.getWriter().write(json);
        } catch (Exception e) {
            System.err.println("ERROR ApiEvacuacionesServlet GET: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        
        try {
            // Leer el cuerpo de la petición
            String body = req.getReader().lines().collect(Collectors.joining());
            System.out.println("DEBUG ApiEvacuacionesServlet POST - Body: " + body);
            
            Map<String, Object> data = gson.fromJson(body, Map.class);
            String zonaId = (String) data.get("zonaId");
            Object personasObj = data.get("personas");
            
            int personas = 0;
            if (personasObj instanceof Double) {
                personas = ((Double) personasObj).intValue();
            } else if (personasObj instanceof Integer) {
                personas = (Integer) personasObj;
            }
            
            System.out.println("DEBUG - Registrar evacuación: zonaId=" + zonaId + ", personas=" + personas);

            // Validaciones
            if (zonaId == null || zonaId.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "El ID de la zona es requerido");
                resp.getWriter().write(gson.toJson(error));
                return;
            }
            
            if (personas <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "El número de personas debe ser mayor a 0");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Buscar la zona
            ZonaAfectada zona = sistema.getGrafo().obtenerZonaPorId(zonaId);
            if (zona == null) {
                System.err.println("ERROR: Zona no encontrada con ID: " + zonaId);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "Zona no encontrada: " + zonaId);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Registrar en la cola de prioridad
            sistema.getColaEvacuaciones().registrarZonaEvacuacion(zona);
            
            // Agregar a la lista de evacuaciones registradas
            EvacuacionInfo evacuacion = new EvacuacionInfo();
            evacuacion.id = "EVAC-" + (evacuacionesRegistradas.size() + 1);
            evacuacion.zonaId = zona.getId();
            evacuacion.zonaNombre = zona.getNombre();
            evacuacion.personas = personas;
            evacuacion.prioridad = zona.getNivelDeRiesgo();
            evacuacion.estado = "PENDIENTE";
            evacuacion.poblacionTotal = zona.getPoblacion();
            
            evacuacionesRegistradas.add(evacuacion);
            
            System.out.println("✅ Evacuación registrada: " + evacuacion.zonaNombre + " - " + evacuacion.personas + " personas");

            // Respuesta exitosa
            JsonObject response = new JsonObject();
            response.addProperty("ok", true);
            response.addProperty("msg", "Evacuación registrada exitosamente");
            response.addProperty("zona", zona.getNombre());
            response.addProperty("prioridad", zona.getNivelDeRiesgo());
            response.addProperty("personas", personas);
            
            resp.getWriter().write(gson.toJson(response));
            
        } catch (Exception e) {
            System.err.println("ERROR ApiEvacuacionesServlet POST: " + e.getMessage());
            e.printStackTrace();
            
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al registrar evacuación: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        
        try {
            // Leer el cuerpo de la petición
            String body = req.getReader().lines().collect(Collectors.joining());
            System.out.println("DEBUG ApiEvacuacionesServlet PUT - Body: " + body);
            
            Map<String, Object> data = gson.fromJson(body, Map.class);
            String accion = (String) data.get("accion");
            
            if ("procesar".equals(accion)) {
                String evacuacionId = (String) data.get("evacuacionId");
                
                System.out.println("DEBUG - Procesar evacuación: " + evacuacionId);
                
                // Buscar la evacuación
                EvacuacionInfo evacuacion = evacuacionesRegistradas.stream()
                    .filter(e -> e.id.equals(evacuacionId))
                    .findFirst()
                    .orElse(null);
                
                if (evacuacion == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    JsonObject error = new JsonObject();
                    error.addProperty("ok", false);
                    error.addProperty("msg", "Evacuación no encontrada");
                    resp.getWriter().write(gson.toJson(error));
                    return;
                }
                
                // Procesar evacuación desde la cola
                ZonaAfectada zonaEvacuada = sistema.getColaEvacuaciones().procesarEvacuacion();
                
                if (zonaEvacuada != null) {
                    // Evacuar las personas de la zona
                    boolean evacuacionExitosa = zonaEvacuada.evacuarPersonas(evacuacion.personas);
                    
                    if (evacuacionExitosa) {
                        System.out.println("✅ Evacuadas " + evacuacion.personas + " personas de " + zonaEvacuada.getNombre());
                        System.out.println("   Población restante: " + zonaEvacuada.getPoblacion());
                        
                        // 🔔 NOTIFICACIÓN: Evacuación completada
                        sistema.agregarNotificacion(
                            TipoNotificacion.EVACUACION_COMPLETADA,
                            "Se evacuaron " + evacuacion.personas + " personas de " + zonaEvacuada.getNombre(),
                            zonaEvacuada.getId()
                        );
                        
                        if (zonaEvacuada.isEvacuada()) {
                            System.out.println("🎯 ZONA COMPLETAMENTE EVACUADA: " + zonaEvacuada.getNombre());
                            
                            // 🔔 NOTIFICACIÓN: Zona completamente evacuada
                            sistema.agregarNotificacion(
                                TipoNotificacion.ZONA_EVACUADA,
                                "La zona " + zonaEvacuada.getNombre() + " ha sido completamente evacuada",
                                zonaEvacuada.getId()
                            );
                        }
                    }
                }
                
                // Actualizar estado de la evacuación
                evacuacion.estado = "COMPLETADA";
                
                System.out.println("✅ Evacuación procesada: " + evacuacion.zonaNombre);
                
                // Respuesta exitosa
                JsonObject response = new JsonObject();
                response.addProperty("ok", true);
                response.addProperty("msg", "Evacuación procesada exitosamente");
                response.addProperty("zona", evacuacion.zonaNombre);
                response.addProperty("personas", evacuacion.personas);
                response.addProperty("estado", evacuacion.estado);
                response.addProperty("poblacionRestante", zonaEvacuada != null ? zonaEvacuada.getPoblacion() : 0);
                response.addProperty("zonaEvacuada", zonaEvacuada != null && zonaEvacuada.isEvacuada());
                
                resp.getWriter().write(gson.toJson(response));
                
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "Acción no válida");
                resp.getWriter().write(gson.toJson(error));
            }
            
        } catch (Exception e) {
            System.err.println("ERROR ApiEvacuacionesServlet PUT: " + e.getMessage());
            e.printStackTrace();
            
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al procesar evacuación: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }
    
    // Clase interna para almacenar información de evacuaciones
    private static class EvacuacionInfo {
        String id;
        String zonaId;
        String zonaNombre;
        int personas;
        int prioridad;
        String estado;
        int poblacionTotal;
    }
}




