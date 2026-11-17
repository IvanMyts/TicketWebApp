package rest;

import dto.request.PageQuery;
import dto.request.TicketRequest;
import service.TicketService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("tickets")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {
    @Inject
    TicketService ticketService;

    @GET
    public Response list(@Valid @BeanParam PageQuery query) {
        return Response.status(200).entity(ticketService.list(query)).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id) {
        return Response.status(200).entity(ticketService.get(id)).build();
    }

    @POST
    public Response create(@NotNull(message = "Тело запроса обязательно") @Valid TicketRequest request) {
        return Response.status(201).entity(ticketService.create(request)).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id,
                                 @NotNull(message = "Тело запроса обязательно") @Valid TicketRequest request) {
        return Response.status(200).entity(ticketService.update(id, request)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        ticketService.delete(id);
        return Response.status(204).build();
    }
}
