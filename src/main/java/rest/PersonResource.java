package rest;

import dto.request.PageQuery;
import dto.request.PersonRequest;
import service.PersonService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("persons")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
    @Inject
    PersonService personService;

    @GET
    public Response list(@Valid @BeanParam PageQuery query) {
        return Response.status(200).entity(personService.list(query)).build();
    }

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") long id) {
        return Response.status(200).entity(personService.get(id)).build();
    }

    @POST
    public Response create(@NotNull(message = "Тело запроса обязательно") @Valid PersonRequest request) {
        return Response.status(201).entity(personService.create(request)).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id,
                             @NotNull(message = "Тело запроса обязательно") @Valid PersonRequest request) {
        return Response.status(200).entity(personService.update(id, request)).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id, @QueryParam("replacementId") Long replacementId) {
        personService.delete(id, replacementId);
        return Response.status(204).build();
    }
}
