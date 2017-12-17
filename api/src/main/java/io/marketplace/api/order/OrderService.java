package io.marketplace.api.order;

import io.marketplace.api.order.model.OrderOverview;
import io.marketplace.api.order.model.OrderRequest;
import io.marketplace.api.order.model.OrderType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

@Component
public class OrderService {
    private final BlockingQueue<OrderRequest> enqueued = new LinkedBlockingDeque<>();
    private final LinkedList<OrderOverview> completed = new LinkedList<>();

    List<OrderOverview> listOrders() {
        return Collections.unmodifiableList(completed);
    }

    boolean placeOrder(OrderRequest incomingOrder) {
        synchronized (enqueued) {
            boolean orderMatched;
            OrderRequest matchingOrder = getMatchingOrder(incomingOrder);
            if (matchingOrder != null) {
                OrderOverview orderOverview = buildOrderOverview(incomingOrder, matchingOrder);
                enqueued.remove(matchingOrder);
                completed.addFirst(orderOverview);
                orderMatched = true;
            } else {
                enqueued.add(incomingOrder);
                orderMatched = false;
            }
            return orderMatched;
        }
    }

    private OrderOverview buildOrderOverview(OrderRequest incomingOrder, OrderRequest matchingOrder) {
        OrderOverview orderOverview;
        if (incomingOrder.getType() == OrderType.BUY) {
            orderOverview = new OrderOverview(incomingOrder, matchingOrder);
        } else {
            orderOverview = new OrderOverview(matchingOrder, incomingOrder);
        }
        return orderOverview;
    }

    private OrderRequest getMatchingOrder(OrderRequest incomingOrder) {
        Stream<OrderRequest> matchingCandidates = enqueued
                .stream()
                .filter(existing -> filterByOppositeType(existing, incomingOrder))
                .filter(existing -> filterByAcceptablePrice(existing, incomingOrder));

        Optional<OrderRequest> optionalMatch;
        if (incomingOrder.getType() == OrderType.SELL) {
            optionalMatch = matchingCandidates.max(Comparator.comparing(OrderRequest::getPrice));
        } else {
            optionalMatch = matchingCandidates.min(Comparator.comparing(OrderRequest::getPrice));
        }
        return optionalMatch.orElse(null);
    }

    private boolean filterByAcceptablePrice(OrderRequest existing, OrderRequest newOrder) {
        boolean satisfied;
        if (existing.getType() == OrderType.BUY) {
            satisfied = existing.getPrice().compareTo(newOrder.getPrice()) > -1;
        } else {
            satisfied = existing.getPrice().compareTo(newOrder.getPrice()) < 1;
        }
        return satisfied;
    }

    private boolean filterByOppositeType(OrderRequest existing, OrderRequest newOrder) {
        return existing.getType() != newOrder.getType();
    }
}
