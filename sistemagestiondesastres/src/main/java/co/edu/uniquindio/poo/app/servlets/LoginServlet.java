package co.edu.uniquindio.poo.app.servlets;

import com.google.gson.Gson;
import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
import co.edu.uniquindio.poo.model.Usuario;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private SistemaGestionDesastres sistema = SistemaGestionDesastres.getInstance();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // CORS headers
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // Leer el cuerpo JSON
        Map<String, String> body = gson.fromJson(req.getReader(), Map.class);
        String email = body.get("email");
        String password = body.get("password");

        boolean ok = sistema.login(email, password);

        if (ok) {
            // Crear sesión después del login exitoso
            String userId = email.toLowerCase().replace("@", "_").replace(".", "_");
            Usuario usuario = sistema.getUsuarios().get(userId);
            
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", userId);
            session.setAttribute("userEmail", email);
            session.setAttribute("userName", usuario != null ? usuario.getNombre() : email);
            session.setAttribute("userRole", usuario != null ? usuario.getRol() : "OPERADOR");
            session.setMaxInactiveInterval(3600); // 1 hora
            
            resp.getWriter().write(gson.toJson(Map.of(
                "ok", true, 
                "msg", "Login correcto",
                "usuario", Map.of(
                    "email", email,
                    "nombre", usuario != null ? usuario.getNombre() : email,
                    "rol", usuario != null ? usuario.getRol() : "OPERADOR"
                )
            )));
        } else {
            resp.getWriter().write(gson.toJson(Map.of("ok", false, "msg", "Credenciales incorrectas")));
        }
    }
}
