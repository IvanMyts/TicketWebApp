package security;

import websocket.ChangesEndpoint;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter({"/api/*", "/ws/*"})
public class AuthFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean open = path.equals("/api/auth/login") || path.equals("/api/auth/register");
        HttpSession session = request.getSession(false);
        if (!open && (session == null || session.getAttribute("login") == null)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"Войдите в систему\"}");
            return;
        }
        chain.doFilter(request, response);
        boolean changing = !"GET".equals(request.getMethod())
                && path.startsWith("/api/") && !path.startsWith("/api/auth/");
        if (changing && response.getStatus() < 400) {
            ChangesEndpoint.broadcast("changed");
        }
    }
}
