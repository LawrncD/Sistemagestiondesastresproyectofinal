package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.TipoRecurso;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/resources")
public class ApiResourcesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        
        // CORS headers
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            Map<String, Map<TipoRecurso, Integer>> recursos =
                    sistema.getMapaRecursos().getRecursosPorUbicacion();
            String json = gson.toJson(recursos);
            System.out.println("DEBUG ApiResourcesServlet GET - Enviando recursos: " + json);
            resp.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR ApiResourcesServlet GET: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al obtener recursos: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");

        try {
            // Leer el cuerpo de la petición
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            System.out.println("DEBUG ApiResourcesServlet POST - Recibido JSON: " + jsonString);

            Map<String, Object> body = gson.fromJson(jsonString, Map.class);
            String accion = (String) body.get("accion");

            System.out.println("DEBUG ApiResourcesServlet POST - Acción: " + accion);

            JsonObject response = new JsonObject();

            // Si la acción es "agregar", agregamos recurso a una zona específica
            if ("agregar".equals(accion)) {
                String ubicacion = (String) body.get("ubicacion");
                String tipoStr = (String) body.get("tipo");
                Object cantidadObj = body.get("cantidad");
                int cantidad = 0;

                // Manejar cantidad como Double o Integer
                if (cantidadObj instanceof Double) {
                    cantidad = ((Double) cantidadObj).intValue();
                } else if (cantidadObj instanceof Integer) {
                    cantidad = (Integer) cantidadObj;
                }

                System.out.println("DEBUG ApiResourcesServlet - Agregar recurso:");
                System.out.println("  Ubicacion: " + ubicacion);
                System.out.println("  Tipo: " + tipoStr);
                System.out.println("  Cantidad: " + cantidad);

                // Validaciones
                if (ubicacion == null || ubicacion.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "La ubicación es requerida");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                if (tipoStr == null || tipoStr.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "El tipo de recurso es requerido");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                if (cantidad <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "La cantidad debe ser mayor a 0");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                // Verificar que la zona existe
                var zona = sistema.getGrafo().obtenerZonaPorId(ubicacion);
                if (zona == null) {
                    System.err.println("ERROR: Zona no encontrada con ID: " + ubicacion);
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "La zona especificada no existe: " + ubicacion);
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                TipoRecurso tipo;
                try {
                    tipo = TipoRecurso.valueOf(tipoStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "Tipo de recurso inválido. Use: ALIMENTO, MEDICINA o AGUA");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                // Usar el NOMBRE de la zona como clave, no el ID
                String ubicacionNombre = zona.getNombre();
                System.out.println("DEBUG: Agregando " + cantidad + " unidades de " + tipo + " a " + ubicacionNombre);
                sistema.getMapaRecursos().agregarRecursos(ubicacionNombre, tipo, cantidad);
                System.out.println("DEBUG: Recurso agregado exitosamente");

                response.addProperty("ok", true);
                response.addProperty("msg", "Recurso agregado exitosamente a " + ubicacionNombre);
                resp.getWriter().write(gson.toJson(response));

            } else {
                // Acción de transferencia (código original)
                String origen = (String) body.get("origen");
                String destino = (String) body.get("destino");
                String tipo = (String) body.get("tipo");
                Object cantidadObj = body.get("cantidad");
                int cantidad = 0;

                if (cantidadObj instanceof Double) {
                    cantidad = ((Double) cantidadObj).intValue();
                } else if (cantidadObj instanceof Integer) {
                    cantidad = (Integer) cantidadObj;
                }

                System.out.println("DEBUG ApiResourcesServlet - Transferir recurso:");
                System.out.println("  Origen: " + origen);
                System.out.println("  Destino: " + destino);
                System.out.println("  Tipo: " + tipo);
                System.out.println("  Cantidad: " + cantidad);

                // Validaciones
                if (origen == null || origen.trim().isEmpty() ||
                        destino == null || destino.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "Origen y destino son requeridos");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                if (tipo == null || tipo.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "El tipo de recurso es requerido");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                if (cantidad <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.addProperty("ok", false);
                    response.addProperty("msg", "La cantidad debe ser mayor a 0");
                    resp.getWriter().write(gson.toJson(response));
                    return;
                }

                boolean ok = sistema.getMapaRecursos().transferirRecursos(
                        origen, destino, TipoRecurso.valueOf(tipo.toUpperCase()), cantidad
                );

                response.addProperty("ok", ok);
                response.addProperty("msg", ok ? "Transferencia exitosa" : "Error en transferencia (recursos insuficientes o ubicación no encontrada)");
                resp.getWriter().write(gson.toJson(response));
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.err.println("ERROR ApiResourcesServlet - Tipo inválido: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Tipo de recurso inválido. Use: ALIMENTO, MEDICINA o AGUA");
            resp.getWriter().write(gson.toJson(error));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR ApiResourcesServlet: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al procesar recursos: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }
}