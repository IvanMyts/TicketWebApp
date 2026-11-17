package rest;

import dto.request.AuthRequest;
import dto.response.AuthResponse;
import dto.response.ErrorResponse;
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
    HttpServletRequest httpRequest;

    @POST
    @Path("register")
    public Response register(@NotNull(message = "Тело запроса обязательно") @Valid AuthRequest request) {
        if (!authService.register(request)) {
            return Response.status(409).entity(new ErrorResponse("Этот логин уже занят")).build();
        }
        return Response.status(201).entity(new AuthResponse(request.getLogin())).build();
    }

    @POST
    @Path("login")
    public Response login(@NotNull(message = "Тело запроса обязательно") @Valid AuthRequest request) {
        if (!authService.authenticate(request)) {
            return Response.status(401).entity(new ErrorResponse("Неверный логин или пароль")).build();
        }
        HttpSession old = httpRequest.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        httpRequest.getSession(true).setAttribute("login", request.getLogin());
        return Response.status(200).entity(new AuthResponse(request.getLogin())).build();
    }

    @GET
    @Path("me")
    public Response me() {
        return Response.status(200).entity(new AuthResponse((String) httpRequest.getSession().getAttribute("login"))).build();
    }

    @POST
    @Path("logout")
    public Response logout() {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.status(204).build();
    }
}
