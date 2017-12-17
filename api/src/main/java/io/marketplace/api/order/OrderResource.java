package io.marketplace.api.order;

import io.marketplace.api.order.model.OrderRequest;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("orders")
public class OrderResource {
    @Inject
    private OrderService service;

    @GET
    public Response listOrders() {
        return Response
                .ok(service.listOrders())
                .build();
    }

    @POST
    public Response placeOrder(@Valid @NotNull(message = "{OrderResource.request.null}") OrderRequest orderRequest) {
        boolean orderMatched = service.placeOrder(orderRequest);
        return Response
                .status(resolveStatus(orderMatched))
                .build();
    }

    private Response.Status resolveStatus(boolean orderMatched) {
        return orderMatched ? Response.Status.OK : Response.Status.ACCEPTED;
    }
}
