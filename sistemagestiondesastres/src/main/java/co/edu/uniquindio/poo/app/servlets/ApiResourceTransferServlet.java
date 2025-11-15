package co.edu.uniquindio.poo.app.servlets;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.Notificacion.TipoNotificacion;
import co.edu.uniquindio.poo.model.TipoRecurso;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/resources/transfer")
public class ApiResourceTransferServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        try {
            // Leer el cuerpo de la petición
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = req.getReader().readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            System.out.println("DEBUG ApiResourceTransferServlet POST - Recibido JSON: " + jsonString);

            Map<String, Object> body = gson.fromJson(jsonString, Map.class);
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

            System.out.println("DEBUG ApiResourceTransferServlet - Transferir recurso:");
            System.out.println("  Origen: " + origen);
            System.out.println("  Destino: " + destino);
            System.out.println("  Tipo: " + tipo);
            System.out.println("  Cantidad: " + cantidad);

            // Validaciones
            if (origen == null || origen.trim().isEmpty() ||
                    destino == null || destino.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "Origen y destino son requeridos");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            if (tipo == null || tipo.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "El tipo de recurso es requerido");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            if (cantidad <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "La cantidad debe ser mayor a 0");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            TipoRecurso tipoRecurso;
            try {
                tipoRecurso = TipoRecurso.valueOf(tipo.toUpperCase());
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject error = new JsonObject();
                error.addProperty("ok", false);
                error.addProperty("msg", "Tipo de recurso inválido. Use: ALIMENTO, MEDICINA o AGUA");
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            boolean ok = sistema.getMapaRecursos().transferirRecursos(
                    origen, destino, tipoRecurso, cantidad
            );

            // 🔔 NOTIFICACIÓN: Verificar recursos bajos en destino
            if (ok) {
                var recursosDestino = sistema.getMapaRecursos().getRecursosUbicacion(destino);
                if (recursosDestino != null) {
                    int cantidadActual = recursosDestino.getOrDefault(tipoRecurso, 0);
                    
                    // Umbral de recursos bajos: menos de 200 unidades
                    if (cantidadActual < 200) {
                        sistema.agregarNotificacion(
                            TipoNotificacion.RECURSOS_BAJOS,
                            "⚠️ Recursos bajos en " + destino + ": " + tipoRecurso + " = " + cantidadActual + " unidades",
                            destino
                        );
                    }
                }
            }

            JsonObject response = new JsonObject();
            response.addProperty("ok", ok);
            response.addProperty("msg", ok ? "Transferencia exitosa" : "Error en transferencia (recursos insuficientes o ubicación no encontrada)");
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR ApiResourceTransferServlet: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("ok", false);
            error.addProperty("msg", "Error al transferir recursos: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
