package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.Notificacion.TipoNotificacion;
import co.edu.uniquindio.poo.model.Ruta;
import co.edu.uniquindio.poo.model.ZonaAfectada;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/simulaciones/*")
public class ApiSimulacionesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // CORS headers
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String pathInfo = req.getPathInfo();
        
        System.out.println("========================================");
        System.out.println("🔔 ApiSimulacionesServlet - POST Request");
        System.out.println("📍 Path: " + pathInfo);
        System.out.println("🌐 URL: " + req.getRequestURL());
        
        try {
            if ("/desastre".equals(pathInfo)) {
                simularDesastre(req, resp);
            } else if ("/evacuacion".equals(pathInfo)) {
                simularEvacuacion(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Endpoint no encontrado\"}");
            }
        } catch (Exception e) {
            System.err.println("Error en simulación: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void simularDesastre(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("========================================");
        System.out.println("🌋 SERVLET: Recibida petición de simulación de desastre");
        
        String body = req.getReader().lines().collect(Collectors.joining());
        System.out.println("📄 Body recibido: " + body);
        
        JsonObject json = gson.fromJson(body, JsonObject.class);
        
        String tipoDesastre = json.get("tipoDesastre").getAsString();
        int intensidad = json.get("intensidad").getAsInt();
        List<String> zonasAfectadasIds = new ArrayList<>();
        json.get("zonasAfectadas").getAsJsonArray().forEach(e -> zonasAfectadasIds.add(e.getAsString()));
        
        System.out.println("🌋 Simulando desastre: " + tipoDesastre + " con intensidad " + intensidad + "%");
        System.out.println("📍 Zonas afectadas: " + zonasAfectadasIds.size());
        
        // Calcular incremento de riesgo según tipo de desastre e intensidad
        int baseIncremento = intensidad / 2; // Base: la mitad de la intensidad
        double multiplicador = 1.0;
        
        switch (tipoDesastre) {
            case "TERREMOTO":
                multiplicador = 1.5;
                break;
            case "HURACAN":
                multiplicador = 1.4;
                break;
            case "INUNDACION":
                multiplicador = 1.2;
                break;
            case "INCENDIO":
                multiplicador = 1.3;
                break;
            case "DESLIZAMIENTO":
                multiplicador = 1.25;
                break;
            case "SEQUIA":
                multiplicador = 0.8;
                break;
        }
        
        int incrementoRiesgo = (int) (baseIncremento * multiplicador);
        
        // Aplicar el desastre a las zonas
        List<JsonObject> zonasAfectadasResult = new ArrayList<>();
        List<String> notificacionesGeneradas = new ArrayList<>();
        int poblacionTotalAfectada = 0;
        
        for (String zonaId : zonasAfectadasIds) {
            ZonaAfectada zona = sistema.getGrafo().obtenerZonaPorId(zonaId);
            if (zona != null) {
                int nivelRiesgoAnterior = zona.getNivelDeRiesgo();
                int nivelRiesgoNuevo = (int) Math.min(100, nivelRiesgoAnterior + incrementoRiesgo);
                
                // Actualizar riesgo
                zona.setNivelDeRiesgo(nivelRiesgoNuevo);
                
                // Calcular población afectada
                int poblacionAfectada = (zona.getPoblacion() * nivelRiesgoNuevo) / 100;
                poblacionTotalAfectada += poblacionAfectada;
                
                // Generar notificación si es zona crítica
                boolean esCritica = nivelRiesgoNuevo >= 80;
                if (esCritica) {
                    sistema.agregarNotificacion(
                        TipoNotificacion.RIESGO_CRITICO,
                        "⚠️ ZONA CRÍTICA: " + zona.getNombre() + 
                        " alcanzó nivel de riesgo de " + nivelRiesgoNuevo + 
                        "% tras " + tipoDesastre.toLowerCase(),
                        zonaId
                    );
                    notificacionesGeneradas.add("Alerta crítica generada para " + zona.getNombre());
                }
                
                // Construir resultado de la zona
                JsonObject zonaResult = new JsonObject();
                zonaResult.addProperty("nombreZona", zona.getNombre());
                zonaResult.addProperty("nivelRiesgoAnterior", nivelRiesgoAnterior);
                zonaResult.addProperty("nivelRiesgoNuevo", nivelRiesgoNuevo);
                zonaResult.addProperty("poblacionAfectada", poblacionAfectada);
                zonaResult.addProperty("critica", esCritica);
                
                zonasAfectadasResult.add(zonaResult);
                
                System.out.println("  📊 " + zona.getNombre() + ": " + nivelRiesgoAnterior + "% → " + nivelRiesgoNuevo + "% " + (esCritica ? "🚨 CRÍTICA" : ""));
            } else {
                System.out.println("  ⚠️ Zona no encontrada: " + zonaId);
            }
        }
        
        // Construir respuesta
        JsonObject response = new JsonObject();
        response.addProperty("tipoDesastre", tipoDesastre);
        response.addProperty("intensidad", intensidad);
        response.addProperty("poblacionTotalAfectada", poblacionTotalAfectada);
        response.add("zonasAfectadas", gson.toJsonTree(zonasAfectadasResult));
        response.add("notificacionesGeneradas", gson.toJsonTree(notificacionesGeneradas));
        
        System.out.println("✅ Simulación de desastre completada");
        System.out.println("📤 Enviando respuesta: " + response.toString());
        System.out.println("========================================");
        
        resp.getWriter().write(gson.toJson(response));
        resp.getWriter().flush();
    }

    private void simularEvacuacion(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("========================================");
        System.out.println("🚑 SERVLET: Recibida petición de simulación de evacuación");
        
        String body = req.getReader().lines().collect(Collectors.joining());
        System.out.println("📄 Body recibido: " + body);
        
        JsonObject json = gson.fromJson(body, JsonObject.class);
        
        String zonaOrigenId = json.get("zonaOrigenId").getAsString();
        String zonaDestinoId = json.get("zonaDestinoId").getAsString();
        int numeroPersonas = json.get("numeroPersonas").getAsInt();
        
        System.out.println("🚑 Planificando evacuación de " + numeroPersonas + " personas");
        System.out.println("📍 Origen: " + zonaOrigenId + " → Destino: " + zonaDestinoId);
        
        ZonaAfectada zonaOrigen = sistema.getGrafo().obtenerZonaPorId(zonaOrigenId);
        ZonaAfectada zonaDestino = sistema.getGrafo().obtenerZonaPorId(zonaDestinoId);
        
        if (zonaOrigen == null || zonaDestino == null) {
            System.out.println("❌ Zona no encontrada");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Zona no encontrada\"}");
            resp.getWriter().flush();
            return;
        }
        
        System.out.println("✅ Zonas encontradas:");
        System.out.println("  Origen: " + zonaOrigen.getNombre());
        System.out.println("  Destino: " + zonaDestino.getNombre());
        
        // Intentar calcular ruta óptima
        List<Ruta> rutaRutas = sistema.getGrafo().obtenerRutaMasCorta(zonaOrigenId, zonaDestinoId);
        
        // Variables para la ruta
        List<String> zonasIds = new ArrayList<>();
        List<String> rutaNombres = new ArrayList<>();
        double distanciaKm = 0;
        
        if (rutaRutas != null && !rutaRutas.isEmpty()) {
            // Hay ruta registrada - usar la ruta del grafo
            System.out.println("✅ Ruta encontrada en el grafo con " + rutaRutas.size() + " segmentos");
            
            zonasIds.add(zonaOrigenId);
            for (Ruta r : rutaRutas) {
                if (!zonasIds.contains(r.getDestinoId())) {
                    zonasIds.add(r.getDestinoId());
                }
                distanciaKm += r.getDistancia();
            }
            
            // Convertir IDs a nombres
            for (String zonaId : zonasIds) {
                ZonaAfectada z = sistema.getGrafo().obtenerZonaPorId(zonaId);
                if (z != null) {
                    rutaNombres.add(z.getNombre());
                }
            }
        } else {
            // No hay ruta registrada - crear ruta directa calculada
            System.out.println("⚠️ No hay ruta en el grafo - calculando ruta directa");
            
            zonasIds.add(zonaOrigenId);
            zonasIds.add(zonaDestinoId);
            
            rutaNombres.add(zonaOrigen.getNombre());
            rutaNombres.add(zonaDestino.getNombre());
            
            // Calcular distancia directa usando coordenadas
            if (zonaOrigen.getLat() != 0 && zonaOrigen.getLng() != 0 && 
                zonaDestino.getLat() != 0 && zonaDestino.getLng() != 0) {
                distanciaKm = calcularDistancia(
                    zonaOrigen.getLat(), zonaOrigen.getLng(),
                    zonaDestino.getLat(), zonaDestino.getLng()
                );
                System.out.println("📏 Distancia calculada: " + distanciaKm + " km");
            } else {
                // Si no hay coordenadas, usar distancia estimada
                distanciaKm = 50.0; // Distancia por defecto
                System.out.println("📏 Usando distancia estimada: " + distanciaKm + " km");
            }
        }
        
        System.out.println("🗺️ Ruta final: " + String.join(" → ", rutaNombres));
        System.out.println("📏 Distancia total: " + distanciaKm + " km");
        
        // Calcular vehículos necesarios
        int vehiculosNecesarios = (numeroPersonas + 49) / 50; // 50 personas por vehículo
        
        // Calcular tiempo estimado
        double tiempoViajeHoras = distanciaKm / 40.0;
        double tiempoOperacionHoras = (numeroPersonas / 1000.0) * 0.5;
        double tiempoEstimadoHoras = (tiempoViajeHoras + tiempoOperacionHoras) * 1.5;
        
        // Calcular recursos necesarios
        List<JsonObject> recursosNecesarios = new ArrayList<>();
        recursosNecesarios.add(crearRecurso("AGUA", numeroPersonas * 3));
        recursosNecesarios.add(crearRecurso("ALIMENTO", numeroPersonas * 2));
        recursosNecesarios.add(crearRecurso("MEDICINA", numeroPersonas / 10));
        recursosNecesarios.add(crearRecurso("MANTAS", numeroPersonas));
        recursosNecesarios.add(crearRecurso("EQUIPO_RESCATE", vehiculosNecesarios * 2));
        recursosNecesarios.add(crearRecurso("COMBUSTIBLE", (int)(distanciaKm * vehiculosNecesarios * 2)));
        
        // Generar plan de ejecución
        List<String> planEjecucion = new ArrayList<>();
        planEjecucion.add("Movilizar " + vehiculosNecesarios + " vehículos de evacuación al punto de origen");
        planEjecucion.add("Establecer puntos de reunión y coordinar con equipos de rescate locales");
        planEjecucion.add("Evacuar " + numeroPersonas + " personas siguiendo la ruta: " + String.join(" → ", rutaNombres));
        planEjecucion.add("Distribuir recursos en zona de destino y establecer centro de atención temporal");
        
        // Construir respuesta
        JsonObject response = new JsonObject();
        response.addProperty("zonaOrigen", zonaOrigen.getNombre());
        response.addProperty("zonaDestino", zonaDestino.getNombre());
        response.addProperty("numeroPersonas", numeroPersonas);
        response.add("ruta", gson.toJsonTree(rutaNombres));
        response.addProperty("distanciaKm", Math.round(distanciaKm * 10) / 10.0);
        response.addProperty("vehiculosNecesarios", vehiculosNecesarios);
        response.addProperty("tiempoEstimadoHoras", Math.round(tiempoEstimadoHoras * 10) / 10.0);
        response.add("recursosNecesarios", gson.toJsonTree(recursosNecesarios));
        response.add("planEjecucion", gson.toJsonTree(planEjecucion));
        
        System.out.println("✅ Plan de evacuación generado");
        System.out.println("📤 Enviando respuesta: " + response.toString());
        System.out.println("========================================");
        
        resp.getWriter().write(gson.toJson(response));
        resp.getWriter().flush();
    }
    
    private JsonObject crearRecurso(String tipo, int cantidad) {
        JsonObject recurso = new JsonObject();
        recurso.addProperty("tipo", tipo);
        recurso.addProperty("cantidad", cantidad);
        return recurso;
    }
    
    private double calcularDistancia(double lat1, double lng1, double lat2, double lng2) {
        // Fórmula de Haversine para calcular distancia entre dos puntos
        final int R = 6371; // Radio de la Tierra en km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // CORS preflight
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
