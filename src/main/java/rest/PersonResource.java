package rest;

import dto.request.PageQuery;
import dto.response.PageResponse;
import dto.request.PersonRequest;
import dto.response.PersonResponse;
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
    PersonService service;

    @GET
    public PageResponse<PersonResponse> list(@Valid @BeanParam PageQuery query) {
        return service.list(query);
    }

    @GET
    @Path("{id}")
    public PersonResponse get(@PathParam("id") long id) {
        return service.get(id);
    }

    @POST
    public Response create(@NotNull(message = "Тело запроса обязательно") @Valid PersonRequest request) {
        return Response.status(Response.Status.CREATED).entity(service.create(request)).build();
    }

    @PUT
    @Path("{id}")
    public PersonResponse update(@PathParam("id") long id,
                             @NotNull(message = "Тело запроса обязательно") @Valid PersonRequest request) {
        return service.update(id, request);
    }

    /** replacementId — объект, к которому перепривязываются ссылки на удаляемый. */
    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") long id, @QueryParam("replacementId") Long replacementId) {
        service.delete(id, replacementId);
    }
}
