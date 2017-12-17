package io.marketplace.api.trade;

import io.marketplace.api.trade.model.CompletedTrade;
import io.marketplace.api.trade.model.TradeOrder;
import io.marketplace.api.trade.model.TradeType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

@Component
public class TradeService {
    private final BlockingQueue<TradeOrder> enqueued = new LinkedBlockingDeque<>();
    private final LinkedList<CompletedTrade> completed = new LinkedList<>();

    List<CompletedTrade> completedTrades() {
        return Collections.unmodifiableList(completed);
    }

    boolean placeOrder(TradeOrder incomingOrder) {
        synchronized (enqueued) {
            boolean orderMatched;
            Optional<TradeOrder> optionalMatch = findMatchingOrder(incomingOrder);
            if (optionalMatch.isPresent()) {
                TradeOrder matchingOrder = optionalMatch.get();
                CompletedTrade completedTrade = buildCompletedTrade(incomingOrder, matchingOrder);
                enqueued.remove(matchingOrder);
                completed.addFirst(completedTrade);
                orderMatched = true;
            } else {
                enqueued.add(incomingOrder);
                orderMatched = false;
            }
            return orderMatched;
        }
    }

    private CompletedTrade buildCompletedTrade(TradeOrder incomingOrder, TradeOrder matchingOrder) {
        CompletedTrade completedTrade;
        if (incomingOrder.getType() == TradeType.BUY) {
            completedTrade = new CompletedTrade(incomingOrder, matchingOrder);
        } else {
            completedTrade = new CompletedTrade(matchingOrder, incomingOrder);
        }
        return completedTrade;
    }

    private Optional<TradeOrder> findMatchingOrder(TradeOrder incomingOrder) {
        Stream<TradeOrder> matchingCandidates = enqueued
                .stream()
                .filter(existing -> filterByOppositeType(existing, incomingOrder))
                .filter(existing -> filterByAcceptablePrice(existing, incomingOrder));

        Optional<TradeOrder> optionalMatch;
        if (incomingOrder.getType() == TradeType.BUY) {
            optionalMatch = matchingCandidates.min(Comparator.comparing(TradeOrder::getPrice));
        } else {
            optionalMatch = matchingCandidates.max(Comparator.comparing(TradeOrder::getPrice));
        }
        return optionalMatch;
    }

    private boolean filterByAcceptablePrice(TradeOrder existingOrder, TradeOrder incomingOrder) {
        boolean acceptablePrice;
        if (existingOrder.getType() == TradeType.BUY) {
            acceptablePrice = isSellPriceLessThenOrEqualToBuyPrice(existingOrder, incomingOrder);
        } else {
            acceptablePrice = isSellPriceLessThenOrEqualToBuyPrice(incomingOrder, existingOrder);
        }
        return acceptablePrice;
    }

    private boolean isSellPriceLessThenOrEqualToBuyPrice(TradeOrder buyOrder, TradeOrder sellOrder) {
        return buyOrder.getPrice().compareTo(sellOrder.getPrice()) > -1;
    }

    private boolean filterByOppositeType(TradeOrder existing, TradeOrder newOrder) {
        return existing.getType() != newOrder.getType();
    }
}
