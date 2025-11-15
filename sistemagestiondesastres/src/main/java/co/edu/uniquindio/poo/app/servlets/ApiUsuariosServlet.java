package co.edu.uniquindio.poo.app.servlets;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.*;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servlet para gestionar la lista de usuarios del sistema
 * GET: Retorna todos los usuarios registrados con su información
 */
public class ApiUsuariosServlet extends HttpServlet {
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
            SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
            Map<String, Usuario> usuarios = sistema.getUsuarios();
            
            // Convertir usuarios a formato DTO para no exponer contraseñas
            List<UsuarioInfo> usuariosInfo = usuarios.values().stream()
                .map(u -> {
                    String email = u.getId().replace("_", "@").replace("__", ".");
                    
                    // Detectar rol por tipo de instancia
                    String rol = "OPERADOR";
                    if (u instanceof Admin) {
                        rol = "ADMIN";
                    } else if (u instanceof OperadorDeEmergencia) {
                        rol = "OPERADOR";
                    }
                    
                    return new UsuarioInfo(
                        u.getId(),
                        u.getNombre(),
                        email,
                        rol,
                        "ACTIVO" // Por ahora todos activos
                    );
                })
                .collect(Collectors.toList());
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(usuariosInfo));
            
        } catch (Exception e) {
            System.err.println("Error en ApiUsuariosServlet: " + e.getMessage());
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of(
                "error", "Error al obtener usuarios: " + e.getMessage()
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
    
    /**
     * Clase interna para representar información de usuario sin datos sensibles
     */
    private static class UsuarioInfo {
        private final String id;
        private final String nombre;
        private final String email;
        private final String rol;
        private final String estado;
        
        public UsuarioInfo(String id, String nombre, String email, String rol, String estado) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.rol = rol;
            this.estado = estado;
        }
    }
}
