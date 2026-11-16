package rest;

import dto.request.PageQuery;
import dto.response.PageResponse;
import dto.request.VenueRequest;
import dto.response.VenueResponse;
import service.VenueService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("venues")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VenueResource {
    @Inject
    VenueService service;

    @GET
    public PageResponse<VenueResponse> list(@Valid @BeanParam PageQuery query) {
        return service.list(query);
    }

    @GET
    @Path("{id}")
    public VenueResponse get(@PathParam("id") int id) {
        return service.get(id);
    }

    @POST
    public Response create(@NotNull(message = "Тело запроса обязательно") @Valid VenueRequest request) {
        return Response.status(Response.Status.CREATED).entity(service.create(request)).build();
    }

    @PUT
    @Path("{id}")
    public VenueResponse update(@PathParam("id") int id,
                             @NotNull(message = "Тело запроса обязательно") @Valid VenueRequest request) {
        return service.update(id, request);
    }

    /** replacementId — объект, к которому перепривязываются ссылки на удаляемый. */
    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") int id, @QueryParam("replacementId") Integer replacementId) {
        service.delete(id, replacementId);
    }
}
