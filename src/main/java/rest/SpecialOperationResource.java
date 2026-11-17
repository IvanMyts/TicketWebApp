package rest;

import dto.response.CountResponse;
import dto.request.SellTicketRequest;
import dto.response.TicketResponse;
import service.SpecialOperationService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("operations")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpecialOperationResource {
    @Inject
    SpecialOperationService service;

    @GET
    @Path("count-number-less-than")
    public CountResponse countByNumberLessThan(
            @NotNull(message = "Укажите число") @QueryParam("number") Double number) {
        return service.countByNumberLessThan(number);
    }

    @GET
    @Path("name-starts-with")
    public List<TicketResponse> findByNamePrefix(
            @NotNull(message = "Укажите подстроку") @QueryParam("prefix") String prefix) {
        return service.findByNamePrefix(prefix);
    }

    @GET
    @Path("distinct-numbers")
    public List<Double> distinctNumbers() {
        return service.distinctNumbers();
    }

    @POST
    @Path("tickets/{id}/sell")
    public TicketResponse sell(@PathParam("id") long id,
                               @NotNull(message = "Тело запроса обязательно") @Valid SellTicketRequest request) {
        return service.sell(id, request);
    }

    @POST
    @Path("tickets/{id}/copy-vip")
    public Response copyAsVip(@PathParam("id") long id) {
        return Response.status(Response.Status.CREATED).entity(service.copyAsVip(id)).build();
    }
}
