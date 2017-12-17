package io.marketplace.api.trade.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CompletedTrade {
    private TradeOrder buyOrder;
    private TradeOrder sellOrder;

    public CompletedTrade() {
    }

    public CompletedTrade(TradeOrder buyOrder, TradeOrder sellOrder) {
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
    }

    public TradeOrder getBuyOrder() {
        return buyOrder;
    }

    public TradeOrder getSellOrder() {
        return sellOrder;
    }
}