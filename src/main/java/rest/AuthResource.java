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

@Path("auth")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    @Inject
    AuthService authService;
    @Context
    HttpServletRequest request;

    @POST
    @Path("register")
    public Response register(@NotNull(message = "Тело запроса обязательно") @Valid AuthRequest body) {
        if (!authService.register(body)) {
            return ErrorMapper.response(409, "Этот логин уже занят");
        }
        return Response.status(Response.Status.CREATED).entity(new AuthResponse(body.getLogin())).build();
    }

    @POST
    @Path("login")
    public Response login(@NotNull(message = "Тело запроса обязательно") @Valid AuthRequest body) {
        if (!authService.authenticate(body)) {
            return ErrorMapper.response(401, "Неверный логин или пароль");
        }
        HttpSession old = request.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        request.getSession(true).setAttribute("login", body.getLogin());
        return Response.ok(new AuthResponse(body.getLogin())).build();
    }

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
