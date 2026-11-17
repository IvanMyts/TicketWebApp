package rest;

import dto.request.PageQuery;
import dto.request.LocationRequest;
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
    public Response list(@Valid @BeanParam PageQuery query) {
        return Response.status(200).entity(service.list(query)).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id) {
        return Response.status(200).entity(service.get(id)).build();
    }

    @POST
    public Response create(@NotNull(message = "Тело запроса обязательно") @Valid LocationRequest request) {
        return Response.status(201).entity(service.create(request)).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id,
                             @NotNull(message = "Тело запроса обязательно") @Valid LocationRequest request) {
        return Response.status(200).entity(service.update(id, request)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id, @QueryParam("replacementId") Long replacementId) {
        service.delete(id, replacementId);
        return Response.status(204).build();
    }
}
