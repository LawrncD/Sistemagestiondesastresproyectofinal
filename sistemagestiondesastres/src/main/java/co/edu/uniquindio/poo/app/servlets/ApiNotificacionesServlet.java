package co.edu.uniquindio.poo.app.servlets;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.Notificacion;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet REST para la gestión de notificaciones del sistema.
 * 
 * Proporciona endpoints para consultar, marcar como leídas y gestionar
 * las notificaciones generadas por eventos del sistema de desastres.
 * @author Sistema de Gestión de Desastres - Universidad del Quindío
 * @version 1.0
 * @since 2025
 */
@WebServlet("/api/notificaciones")
public class ApiNotificacionesServlet extends HttpServlet {
    private final SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private final Gson gson = new Gson();

    /**
     * Maneja solicitudes GET para obtener todas las notificaciones.
     * 
     * Retorna un objeto JSON con el total de notificaciones, cantidad de no leídas
     * y un array detallado de cada notificación con sus atributos completos.
     * 
     * @param req Objeto HttpServletRequest con los parámetros de la petición
     * @param resp Objeto HttpServletResponse para enviar la respuesta
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de entrada/salida
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<Notificacion> notificaciones = sistema.obtenerNotificaciones();
            
            // Construir respuesta JSON con información adicional
            JsonObject response = new JsonObject();
            response.addProperty("total", notificaciones.size());
            response.addProperty("noLeidas", sistema.contarNotificacionesNoLeidas());
            response.add("notificaciones", gson.toJsonTree(notificaciones.stream().map(n -> {
                JsonObject notif = new JsonObject();
                notif.addProperty("id", n.getId());
                notif.addProperty("tipo", n.getTipo().name());
                notif.addProperty("tipoClase", n.getTipo().getClase());
                notif.addProperty("tipoIcono", n.getTipo().getIcono());
                notif.addProperty("mensaje", n.getMensaje());
                notif.addProperty("timestamp", n.getTimestampFormateado());
                notif.addProperty("leida", n.isLeida());
                notif.addProperty("zonaRelacionada", n.getZonaRelacionada());
                return notif;
            }).toArray()));

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Error al obtener notificaciones: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    /**
     * Maneja solicitudes PUT para marcar una notificación como leída.
     * 
     * Espera un cuerpo JSON con el campo "id" de la notificación a marcar.
     * Retorna el conteo actualizado de notificaciones no leídas.
     * 
     * @param req Objeto HttpServletRequest con el ID de la notificación en el cuerpo
     * @param resp Objeto HttpServletResponse para enviar la respuesta
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de entrada/salida
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Leer el ID de la notificación del body
            JsonObject requestBody = gson.fromJson(req.getReader(), JsonObject.class);
            int notifId = requestBody.get("id").getAsInt();

            boolean marcada = sistema.marcarNotificacionComoLeida(notifId);

            JsonObject response = new JsonObject();
            if (marcada) {
                response.addProperty("success", true);
                response.addProperty("mensaje", "Notificación marcada como leída");
                response.addProperty("noLeidas", sistema.contarNotificacionesNoLeidas());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.addProperty("success", false);
                response.addProperty("error", "Notificación no encontrada");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Error al marcar notificación: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    /**
     * Maneja solicitudes DELETE para marcar todas las notificaciones como leídas.
     * 
     * No requiere parámetros. Marca todas las notificaciones existentes como leídas
     * y retorna una confirmación con el contador de no leídas en cero.
     * 
     * @param req Objeto HttpServletRequest con los parámetros de la petición
     * @param resp Objeto HttpServletResponse para enviar la respuesta
     * @throws ServletException Si ocurre un error en el servlet
     * @throws IOException Si ocurre un error de entrada/salida
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            sistema.marcarTodasNotificacionesComoLeidas();

            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.addProperty("mensaje", "Todas las notificaciones marcadas como leídas");
            response.addProperty("noLeidas", 0);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject error = new JsonObject();
            error.addProperty("error", "Error al marcar notificaciones: " + e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }
}
