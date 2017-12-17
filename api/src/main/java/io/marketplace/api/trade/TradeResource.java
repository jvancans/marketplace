package io.marketplace.api.trade;

import io.marketplace.api.trade.model.TradeOrder;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("trades")
public class TradeResource {
    @Inject
    private TradeService service;

    @GET
    public Response completedTrades() {
        return Response
                .ok(service.completedTrades())
                .build();
    }

    @POST
    public Response placeOrder(@Valid @NotNull(message = "{TradeResource.order.null}") TradeOrder tradeOrder) {
        boolean orderMatched = service.placeOrder(tradeOrder);
        return Response
                .status(resolveStatus(orderMatched))
                .build();
    }

    private Response.Status resolveStatus(boolean orderMatched) {
        return orderMatched ? Response.Status.OK : Response.Status.ACCEPTED;
    }
}
