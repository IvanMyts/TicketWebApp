package rest;

import dto.request.PageQuery;
import dto.response.PageResponse;
import dto.request.EventRequest;
import dto.response.EventResponse;
import service.EventService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("events")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {
    @Inject
    EventService service;

    @GET
    public PageResponse<EventResponse> list(@Valid @BeanParam PageQuery query) {
        return service.list(query);
    }

    @GET
    @Path("{id}")
    public EventResponse get(@PathParam("id") int id) {
        return service.get(id);
    }

    @POST
    public Response create(@NotNull(message = "Тело запроса обязательно") @Valid EventRequest request) {
        return Response.status(Response.Status.CREATED).entity(service.create(request)).build();
    }

    @PUT
    @Path("{id}")
    public EventResponse update(@PathParam("id") int id,
                             @NotNull(message = "Тело запроса обязательно") @Valid EventRequest request) {
        return service.update(id, request);
    }

    /** replacementId — объект, к которому перепривязываются ссылки на удаляемый. */
    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") int id, @QueryParam("replacementId") Integer replacementId) {
        service.delete(id, replacementId);
    }
}
