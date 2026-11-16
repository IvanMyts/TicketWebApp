package security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import static rest.ErrorMapper.response;

/** Пропускает к REST только вошедших пользователей. Вход и регистрация открыты. */
@Provider
public class AuthFilter implements ContainerRequestFilter {
    @Context
    HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext context) {
        String path = context.getUriInfo().getPath().replaceFirst("^/+", "");
        if (path.equals("auth/login") || path.equals("auth/register")) {
            return;
        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionUser.ATTRIBUTE) == null) {
            context.abortWith(response(401, "Войдите в систему"));
        }
    }
}
