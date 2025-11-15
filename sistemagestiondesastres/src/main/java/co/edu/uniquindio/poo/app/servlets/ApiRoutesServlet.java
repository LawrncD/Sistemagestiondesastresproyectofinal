package co.edu.uniquindio.poo.app.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.Ruta;
import co.edu.uniquindio.poo.model.ZonaAfectada;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/api/routes")
public class ApiRoutesServlet extends HttpServlet {
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
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            var aristas = sistema.getGrafo().getAristas(); // Map<String, List<Ruta>>

            // Aplanar y enriquecer con nombres de zonas
            List<RutaEnriquecida> listaEnriquecida = new ArrayList<>();

            for (var listaRutas : aristas.values()) {
                for (Ruta ruta : listaRutas) {
                    // Buscar nombres de las zonas
                    ZonaAfectada zonaOrigen = sistema.getGrafo().obtenerZonaPorId(ruta.getOrigenId());
                    ZonaAfectada zonaDestino = sistema.getGrafo().obtenerZonaPorId(ruta.getDestinoId());

                    String nombreOrigen = zonaOrigen != null ? zonaOrigen.getNombre() : ruta.getOrigenId();
                    String nombreDestino = zonaDestino != null ? zonaDestino.getNombre() : ruta.getDestinoId();

                    listaEnriquecida.add(new RutaEnriquecida(
                            ruta.getOrigenId(),
                            ruta.getDestinoId(),
                            nombreOrigen,
                            nombreDestino,
                            ruta.getDistancia(),
                            ruta.getTiempo(),
                            ruta.getCapacidad(),
                            ruta.estaDisponible()
                    ));
                }
            }

            resp.getWriter().write(gson.toJson(listaEnriquecida));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR ApiRoutesServlet GET: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al obtener rutas: " + e.getMessage());
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
            System.out.println("DEBUG ApiRoutesServlet POST - Recibido JSON: " + jsonString);

            // Determinar si es una solicitud de calcular ruta o agregar ruta
            Map<String, Object> body = gson.fromJson(jsonString, Map.class);
            String accion = (String) body.get("accion");

            JsonObject response = new JsonObject();

            if ("calcular".equals(accion)) {
                // Calcular ruta más corta
                String origenId = (String) body.get("origenId");
                String destinoId = (String) body.get("destinoId");

                System.out.println("DEBUG: Calculando ruta de " + origenId + " a " + destinoId);

                List<Ruta> rutaMasCorta = sistema.getGrafo().obtenerRutaMasCorta(origenId, destinoId);

                if (rutaMasCorta == null || rutaMasCorta.isEmpty()) {
                    response.addProperty("ok", false);
                    response.addProperty("msg", "No hay ruta disponible entre esas zonas");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                // Enriquecer rutas con nombres
                List<RutaEnriquecida> rutaEnriquecida = new ArrayList<>();
                double distanciaTotal = 0;
                double tiempoTotal = 0;

                for (Ruta r : rutaMasCorta) {
                    ZonaAfectada zonaOrigen = sistema.getGrafo().obtenerZonaPorId(r.getOrigenId());
                    ZonaAfectada zonaDestino = sistema.getGrafo().obtenerZonaPorId(r.getDestinoId());

                    String nombreOrigen = zonaOrigen != null ? zonaOrigen.getNombre() : r.getOrigenId();
                    String nombreDestino = zonaDestino != null ? zonaDestino.getNombre() : r.getDestinoId();

                    rutaEnriquecida.add(new RutaEnriquecida(
                            r.getOrigenId(),
                            r.getDestinoId(),
                            nombreOrigen,
                            nombreDestino,
                            r.getDistancia(),
                            r.getTiempo(),
                            r.getCapacidad(),
                            r.estaDisponible()
                    ));

                    distanciaTotal += r.getDistancia();
                    tiempoTotal += r.calcularTiempoReal();
                }

                response.addProperty("ok", true);
                response.add("ruta", gson.toJsonTree(rutaEnriquecida));
                response.addProperty("distanciaTotal", distanciaTotal);
                response.addProperty("tiempoTotal", tiempoTotal);
                resp.getWriter().write(gson.toJson(response));

            } else {
                // Agregar nueva ruta
                String origenId = (String) body.get("origenId");
                String destinoId = (String) body.get("destinoId");
                Object distanciaObj = body.get("distancia");
                Object tiempoObj = body.get("tiempo");
                Object capacidadObj = body.get("capacidad");

                double distancia = distanciaObj instanceof Double ? (Double) distanciaObj :
                        ((Number) distanciaObj).doubleValue();
                double tiempo = tiempoObj instanceof Double ? (Double) tiempoObj :
                        ((Number) tiempoObj).doubleValue();
                int capacidad = capacidadObj instanceof Double ? ((Double) capacidadObj).intValue() :
                        ((Number) capacidadObj).intValue();

                System.out.println("DEBUG ApiRoutesServlet - Agregar ruta:");
                System.out.println("  Origen: " + origenId);
                System.out.println("  Destino: " + destinoId);
                System.out.println("  Distancia: " + distancia);
                System.out.println("  Tiempo: " + tiempo);
                System.out.println("  Capacidad: " + capacidad);

                // Validaciones
                if (origenId == null || destinoId == null || origenId.equals(destinoId)) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "Origen y destino deben ser válidos y diferentes");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                if (distancia <= 0 || tiempo <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "Distancia y tiempo deben ser mayores a 0");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                // Verificar que las zonas existen
                ZonaAfectada zonaOrigen = sistema.getGrafo().obtenerZonaPorId(origenId);
                ZonaAfectada zonaDestino = sistema.getGrafo().obtenerZonaPorId(destinoId);

                if (zonaOrigen == null || zonaDestino == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "Una o ambas zonas no existen");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                // Crear y agregar la ruta
                Ruta nuevaRuta = new Ruta(origenId, destinoId, distancia, tiempo, capacidad);
                sistema.getGrafo().agregarArista(nuevaRuta);

                System.out.println("DEBUG: Ruta agregada exitosamente");

                response.addProperty("ok", true);
                response.addProperty("msg", "Ruta creada exitosamente");
                resp.getWriter().write(gson.toJson(response));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR ApiRoutesServlet POST: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al procesar ruta: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    // Clase interna para enriquecer rutas con nombres
    private static class RutaEnriquecida {
        String origenId;
        String destinoId;
        String nombreOrigen;
        String nombreDestino;
        double distancia;
        double tiempo;
        int capacidad;
        boolean disponible;

        RutaEnriquecida(String origenId, String destinoId, String nombreOrigen, String nombreDestino,
                        double distancia, double tiempo, int capacidad, boolean disponible) {
            this.origenId = origenId;
            this.destinoId = destinoId;
            this.nombreOrigen = nombreOrigen;
            this.nombreDestino = nombreDestino;
            this.distancia = distancia;
            this.tiempo = tiempo;
            this.capacidad = capacidad;
            this.disponible = disponible;
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }

            Map<String, Object> body = gson.fromJson(sb.toString(), Map.class);
            String routeId = (String) body.get("id");

            if (routeId == null || routeId.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "El ID de la ruta es requerido");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Buscar la ruta
            Ruta rutaExistente = null;
            String origenId = null;
            for (var entry : sistema.getGrafo().getAristas().entrySet()) {
                origenId = entry.getKey();
                for (Ruta r : entry.getValue()) {
                    String id = r.getOrigenId() + "-" + r.getDestinoId();
                    if (id.equals(routeId)) {
                        rutaExistente = r;
                        break;
                    }
                }
                if (rutaExistente != null) break;
            }

            if (rutaExistente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "Ruta no encontrada");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Como Ruta es inmutable, crear una nueva ruta con los valores actualizados
            double distancia = body.containsKey("distancia") ? 
                ((Number) body.get("distancia")).doubleValue() : rutaExistente.getDistancia();
            double tiempo = body.containsKey("tiempo") ? 
                ((Number) body.get("tiempo")).doubleValue() : rutaExistente.getTiempo();
            int capacidad = body.containsKey("capacidad") ? 
                ((Number) body.get("capacidad")).intValue() : rutaExistente.getCapacidad();
            
            // Remover la ruta vieja y agregar la nueva
            sistema.getGrafo().getAristas().get(origenId).remove(rutaExistente);
            Ruta nuevaRuta = new Ruta(rutaExistente.getOrigenId(), rutaExistente.getDestinoId(), 
                distancia, tiempo, capacidad);
            sistema.getGrafo().getAristas().get(origenId).add(nuevaRuta);

            JsonObject response = new JsonObject();
            response.addProperty("ok", true);
            response.addProperty("msg", "Ruta actualizada exitosamente");
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al actualizar ruta: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}