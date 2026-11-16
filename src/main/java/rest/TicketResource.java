package rest;

import dto.request.PageQuery;
import dto.response.PageResponse;
import dto.request.TicketRequest;
import dto.response.TicketResponse;
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
    public PageResponse<TicketResponse> list(@Valid @BeanParam PageQuery query) {
        return ticketService.list(query);
    }

    @GET
    @Path("{id}")
    public TicketResponse get(@PathParam("id") long id) {
        return ticketService.get(id);
    }

    @POST
    public Response create(@NotNull(message = "Тело запроса обязательно") @Valid TicketRequest request) {
        return Response.status(Response.Status.CREATED).entity(ticketService.create(request)).build();
    }

    @PUT
    @Path("{id}")
    public TicketResponse update(@PathParam("id") long id,
                                 @NotNull(message = "Тело запроса обязательно") @Valid TicketRequest request) {
        return ticketService.update(id, request);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id) {
        ticketService.delete(id);
    }
}
