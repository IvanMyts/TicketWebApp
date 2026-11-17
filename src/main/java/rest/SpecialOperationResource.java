package rest;

import dto.request.SellTicketRequest;
import service.SpecialOperationService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("operations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpecialOperationResource {
    @Inject
    SpecialOperationService specialOperationService;

    @GET
    @Path("count-number-less-than")
    public Response countByNumberLessThan(
            @NotNull(message = "Укажите число") @QueryParam("number") Double number) {
        return Response.status(200).entity(specialOperationService.countByNumberLessThan(number)).build();
    }

    @GET
    @Path("name-starts-with")
    public Response findByNamePrefix(
            @NotNull(message = "Укажите подстроку") @QueryParam("prefix") String prefix) {
        return Response.status(200).entity(specialOperationService.findByNamePrefix(prefix)).build();
    }

    @GET
    @Path("distinct-numbers")
    public Response distinctNumbers() {
        return Response.status(200).entity(specialOperationService.distinctNumbers()).build();
    }

    @POST
    @Path("tickets/{id}/sell")
    public Response sell(@PathParam("id") long id,
                               @NotNull(message = "Тело запроса обязательно") @Valid SellTicketRequest request) {
        return Response.status(200).entity(specialOperationService.sell(id, request)).build();
    }

    @POST
    @Path("tickets/{id}/copy-vip")
    public Response copyAsVip(@PathParam("id") long id) {
        return Response.status(201).entity(specialOperationService.copyAsVip(id)).build();
    }
}
