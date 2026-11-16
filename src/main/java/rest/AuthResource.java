package rest;

import dto.request.AuthRequest;
import dto.response.AuthResponse;
import service.AuthService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** Вход по сессии: после login браузер получает cookie JSESSIONID и шлёт её с каждым запросом. */
@Path("auth")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject
    AuthService authService;
    @Context
    HttpServletRequest request;

    /** 201 — пользователь создан, 409 — логин занят. Входить после регистрации нужно отдельно. */
    @POST
    @Path("register")
    public Response register(@NotNull(message = "Тело запроса обязательно") @Valid AuthRequest body) {
        if (!authService.register(body)) {
            return ErrorMapper.response(409, "Этот логин уже занят");
        }
        return Response.status(Response.Status.CREATED).entity(new AuthResponse(body.getLogin())).build();
    }

    /** 200 — вход выполнен, 401 — неверный логин или пароль. */
    @POST
    @Path("login")
    public Response login(@NotNull(message = "Тело запроса обязательно") @Valid AuthRequest body) {
        if (!authService.authenticate(body)) {
            return ErrorMapper.response(401, "Неверный логин или пароль");
        }
        // Новая сессия при входе, чтобы старый идентификатор сессии нельзя было использовать.
        HttpSession old = request.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        request.getSession(true).setAttribute("login", body.getLogin());
        return Response.ok(new AuthResponse(body.getLogin())).build();
    }

    /** Текущий пользователь. Без входа AuthFilter вернёт 401 — так клиент узнаёт, что нужно войти. */
    @GET
    @Path("me")
    public AuthResponse me() {
        return new AuthResponse((String) request.getSession().getAttribute("login"));
    }

    @POST
    @Path("logout")
    public void logout() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
