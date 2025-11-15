package co.edu.uniquindio.poo.app.servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.Ruta;
import co.edu.uniquindio.poo.model.ZonaAfectada;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet para cálculo de rutas óptimas usando Dijkstra
 * GET /api/optimal-route?origen=ID&destino=ID
 */
public class ApiOptimalRouteServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String origenId = req.getParameter("origen");
        String destinoId = req.getParameter("destino");
        
        if (origenId == null || destinoId == null) {
            resp.setStatus(400);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Parámetros 'origen' y 'destino' requeridos");
            resp.getWriter().write(gson.toJson(error));
            return;
        }
        
        System.out.println("🔍 Calculando ruta óptima: " + origenId + " → " + destinoId);
        
        try {
            SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
            
            // Calcular ruta más corta usando Dijkstra del grafo
            List<Ruta> rutaOptima = sistema.getGrafo().obtenerRutaMasCorta(origenId, destinoId);
            
            if (rutaOptima == null || rutaOptima.isEmpty()) {
                System.out.println("❌ No hay ruta disponible");
                resp.setStatus(404);
                JsonObject error = new JsonObject();
                error.addProperty("error", "No existe una ruta disponible entre estas zonas");
                error.addProperty("msg", "No hay camino disponible");
                resp.getWriter().write(gson.toJson(error));
                return;
            }
            
            // Calcular estadísticas de la ruta
            double distanciaTotal = rutaOptima.stream().mapToDouble(Ruta::getDistancia).sum();
            double tiempoTotal = rutaOptima.stream().mapToDouble(Ruta::getTiempo).sum();
            int capacidadMinima = rutaOptima.stream().mapToInt(Ruta::getCapacidad).min().orElse(0);
            
            // Obtener información de zonas
            ZonaAfectada zonaOrigen = sistema.getGrafo().obtenerZonaPorId(origenId);
            ZonaAfectada zonaDestino = sistema.getGrafo().obtenerZonaPorId(destinoId);
            
            // Preparar respuesta
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("origen", origenId);
            resultado.put("destino", destinoId);
            resultado.put("origenNombre", zonaOrigen != null ? zonaOrigen.getNombre() : origenId);
            resultado.put("destinoNombre", zonaDestino != null ? zonaDestino.getNombre() : destinoId);
            resultado.put("distanciaTotal", Math.round(distanciaTotal * 10.0) / 10.0);
            resultado.put("tiempoTotal", Math.round(tiempoTotal * 10.0) / 10.0);
            resultado.put("capacidadMinima", capacidadMinima);
            resultado.put("numeroSegmentos", rutaOptima.size());
            
            // Agregar detalles de cada segmento
            List<Map<String, Object>> segmentos = rutaOptima.stream().map(ruta -> {
                Map<String, Object> segmento = new HashMap<>();
                ZonaAfectada origen = sistema.getGrafo().obtenerZonaPorId(ruta.getOrigenId());
                ZonaAfectada destino = sistema.getGrafo().obtenerZonaPorId(ruta.getDestinoId());
                
                segmento.put("origenId", ruta.getOrigenId());
                segmento.put("destinoId", ruta.getDestinoId());
                segmento.put("origenNombre", origen != null ? origen.getNombre() : ruta.getOrigenId());
                segmento.put("destinoNombre", destino != null ? destino.getNombre() : ruta.getDestinoId());
                segmento.put("distancia", Math.round(ruta.getDistancia() * 10.0) / 10.0);
                segmento.put("tiempo", Math.round(ruta.getTiempo() * 10.0) / 10.0);
                segmento.put("capacidad", ruta.getCapacidad());
                
                return segmento;
            }).collect(Collectors.toList());
            
            resultado.put("segmentos", segmentos);
            
            System.out.println("✅ Ruta óptima calculada: " + distanciaTotal + " km, " + tiempoTotal + " horas");
            
            resp.setStatus(200);
            resp.getWriter().write(gson.toJson(resultado));
            
        } catch (Exception e) {
            System.err.println("❌ Error al calcular ruta óptima: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(500);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Error al calcular ruta: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }
}
