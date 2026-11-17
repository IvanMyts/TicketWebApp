package rest;

import dto.request.PageQuery;
import dto.response.PageResponse;
import dto.request.LocationRequest;
import dto.response.LocationResponse;
import service.LocationService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("locations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocationResource {
    @Inject
    LocationService service;

    @GET
    public PageResponse<LocationResponse> list(@Valid @BeanParam PageQuery query) {
        return service.list(query);
    }

    @GET
    @Path("{id}")
    public LocationResponse get(@PathParam("id") long id) {
        return service.get(id);
    }

    @POST
    public Response create(@NotNull(message = "Тело запроса обязательно") @Valid LocationRequest request) {
        return Response.status(Response.Status.CREATED).entity(service.create(request)).build();
    }

    @PUT
    @Path("{id}")
    public LocationResponse update(@PathParam("id") long id,
                             @NotNull(message = "Тело запроса обязательно") @Valid LocationRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id, @QueryParam("replacementId") Long replacementId) {
        service.delete(id, replacementId);
    }
}
