
package co.edu.uniquindio.poo.app.servlets;

import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.*;
import co.edu.uniquindio.poo.services.ValidationService;
import co.edu.uniquindio.poo.services.SecurityService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

/**
 * Servlet para registro de nuevos usuarios en el sistema
 */
public class RegisterServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        // CORS headers
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        // VERIFICAR RESTRICCIONES DE ROL
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            String currentUserRole = (String) session.getAttribute("userRole");
            if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
                sendError(res, "Solo los administradores pueden registrar nuevos usuarios");
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        // Si no hay sesión, permitir registro público (auto-registro)
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> body = gson.fromJson(req.getReader(), Map.class);
            
            String nombre = body.get("nombre");
            String email = body.get("email");
            String password = body.get("password");
            String telefono = body.getOrDefault("telefono", "");
            String rol = body.getOrDefault("rol", "OPERADOR"); // Default: OPERADOR
            
            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                sendError(res, "El nombre es obligatorio");
                return;
            }
            
            if (!ValidationService.isValidEmail(email)) {
                sendError(res, "Email inválido");
                return;
            }
            
            if (!ValidationService.isValidPassword(password)) {
                sendError(res, "La contraseña debe tener al menos 8 caracteres");
                return;
            }
            
            if (telefono != null && !telefono.trim().isEmpty() && !ValidationService.isValidPhone(telefono)) {
                sendError(res, "Teléfono inválido (debe tener 10 dígitos)");
                return;
            }
            
            // Generar ID único basado en email
            String userId = email.toLowerCase().replace("@", "_").replace(".", "_");
            
            // Verificar si el usuario ya existe
            SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
            if (sistema.getUsuarios().containsKey(userId)) {
                sendError(res, "El email ya está registrado");
                return;
            }
            
            // Crear usuario según rol (guardar contraseña directamente, sin hash)
            Usuario nuevoUsuario;
            if ("ADMIN".equalsIgnoreCase(rol)) {
                nuevoUsuario = new Admin(userId, nombre, password, telefono);
            } else {
                nuevoUsuario = new OperadorDeEmergencia(userId, nombre, password, telefono);
            }
            
            // Agregar al sistema
            sistema.registrarUsuario(nuevoUsuario);
            
            // Crear sesión automáticamente después del registro (auto-login)
            // Reutilizar la variable session o crear una nueva si no existe
            if (session == null) {
                session = req.getSession(true);
            }
            session.setAttribute("userId", userId);
            session.setAttribute("userEmail", email);
            session.setAttribute("userName", nombre);
            session.setAttribute("userRole", rol);
            session.setMaxInactiveInterval(3600); // 1 hora
            
            // Respuesta exitosa con datos de sesión
            res.setStatus(HttpServletResponse.SC_CREATED);
            res.getWriter().write(gson.toJson(Map.of(
                "ok", true,
                "msg", "Usuario registrado exitosamente",
                "autoLogin", true,
                "usuario", Map.of(
                    "id", userId,
                    "nombre", nombre,
                    "email", email,
                    "rol", rol
                )
            )));
            
        } catch (Exception e) {
            System.err.println("Error en RegisterServlet: " + e.getMessage());
            e.printStackTrace();
            sendError(res, "Error al registrar usuario: " + e.getMessage());
        }
    }
    /*
     * Envía una respuesta de error en formato JSON
     */
    
    private void sendError(HttpServletResponse res, String message) throws IOException {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.getWriter().write(gson.toJson(Map.of(
            "ok", false,
            "msg", message
        )));
    }
}
