package io.marketplace.api.order.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrderOverview {
    private OrderRequest buyOrder;
    private OrderRequest sellOrder;


    public OrderOverview() {
    }

    public OrderOverview(OrderRequest buyOrder, OrderRequest sellOrder) {
        this.buyOrder = buyOrder;
        this.sellOrder = sellOrder;
    }

    public OrderRequest getBuyOrder() {
        return buyOrder;
    }

    public OrderRequest getSellOrder() {
        return sellOrder;
    }
}
