package co.edu.uniquindio.poo.app.servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

/**
 * Servlet para verificar si hay una sesión activa
 * GET: Retorna información del usuario en sesión o null
 */
public class ApiSessionServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // CORS headers
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        try {
            HttpSession session = req.getSession(false);
            
            if (session != null) {
                String userId = (String) session.getAttribute("userId");
                String userEmail = (String) session.getAttribute("userEmail");
                String userName = (String) session.getAttribute("userName");
                String userRole = (String) session.getAttribute("userRole");
                
                if (userId != null && userEmail != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(Map.of(
                        "authenticated", true,
                        "user", Map.of(
                            "id", userId,
                            "email", userEmail,
                            "nombre", userName != null ? userName : userEmail,
                            "rol", userRole != null ? userRole : "OPERADOR"
                        )
                    )));
                    return;
                }
            }
            
            // No hay sesión activa
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(Map.of(
                "authenticated", false
            )));
            
        } catch (Exception e) {
            System.err.println("Error en ApiSessionServlet: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of(
                "error", "Error al verificar sesión: " + e.getMessage()
            )));
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
