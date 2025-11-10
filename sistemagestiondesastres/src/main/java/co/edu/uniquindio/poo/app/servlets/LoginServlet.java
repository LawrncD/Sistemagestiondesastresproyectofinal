package co.edu.uniquindio.poo.app.servlets;

import com.google.gson.Gson;
import co.edu.uniquindio.poo.app.SistemaGestionDesastres;
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

        // Leer el cuerpo JSON
        Map<String, String> body = gson.fromJson(req.getReader(), Map.class);
        String email = body.get("email");
        String password = body.get("password");

        boolean ok = sistema.login(email, password);

        if (ok) {
            resp.getWriter().write(gson.toJson(Map.of("ok", true, "msg", "Login correcto")));
        } else {
            resp.getWriter().write(gson.toJson(Map.of("ok", false, "msg", "Credenciales incorrectas")));
        }
    }
}
