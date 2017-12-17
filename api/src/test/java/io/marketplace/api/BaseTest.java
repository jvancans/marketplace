package io.marketplace.api;

import io.marketplace.api.trade.model.TradeOrder;
import io.marketplace.api.trade.model.TradeProduct;
import io.marketplace.api.trade.model.TradeType;

import java.math.BigDecimal;

public abstract class BaseTest {
    protected TradeOrder stubOrder(TradeType type, BigDecimal price) {
        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setProduct(TradeProduct.PUMPKIN);
        tradeOrder.setType(type);
        tradeOrder.setPrice(price);
        return tradeOrder;
    }
}
