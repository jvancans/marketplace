package io.marketplace.api.trade;

import io.marketplace.api.BaseTest;
import io.marketplace.api.trade.model.CompletedTrade;
import io.marketplace.api.trade.model.TradeOrder;
import io.marketplace.api.trade.model.TradeType;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TradeServiceTest extends BaseTest {
    private TradeService service;

    @Before
    public void init() {
        service = new TradeService();
    }

    @Test
    public void testBuyAndSellOrderCompletesForSamePrice() {
        TradeOrder buyOrder = stubOrder(TradeType.BUY, BigDecimal.TEN);
        TradeOrder sellOrder = stubOrder(TradeType.SELL, BigDecimal.ONE);

        boolean buyOrderMatched = service.placeOrder(buyOrder);
        assertThat(buyOrderMatched, is(false));

        boolean sellOrderMatched = service.placeOrder(sellOrder);
        assertThat(sellOrderMatched, is(true));

        List<CompletedTrade> completedTrades = service.completedTrades();
        assertThat(completedTrades, notNullValue());
        assertThat(completedTrades.size(), is(1));

        CompletedTrade completedTrade = completedTrades.get(0);
        assertThat(completedTrade.getBuyOrder(), equalTo(buyOrder));
        assertThat(completedTrade.getSellOrder(), equalTo(sellOrder));
    }

    @Test
    public void testBuyOrderDoesNotCompleteIfSellPriceIsTooHigh() {
        TradeOrder buyOrder = stubOrder(TradeType.BUY, BigDecimal.ONE);
        TradeOrder sellOrder = stubOrder(TradeType.SELL, BigDecimal.TEN);

        boolean buyOrderMatched = service.placeOrder(buyOrder);
        assertThat(buyOrderMatched, is(false));

        boolean sellOrderMatched = service.placeOrder(sellOrder);
        assertThat(sellOrderMatched, is(false));

        List<CompletedTrade> completedTrades = service.completedTrades();
        assertThat(completedTrades, notNullValue());
        assertThat(completedTrades, empty());
    }

    @Test
    public void testSellOrderMatchesHighestBuyOrder() {
        TradeOrder lowBuyOrder = stubOrder(TradeType.BUY, BigDecimal.TEN);
        TradeOrder highBuyOrder = stubOrder(TradeType.BUY, BigDecimal.valueOf(100));
        TradeOrder sellOrder = stubOrder(TradeType.SELL, BigDecimal.ONE);

        boolean lowBuyOrderMatched = service.placeOrder(lowBuyOrder);
        assertThat(lowBuyOrderMatched, is(false));

        boolean highBuyOrderMatched = service.placeOrder(highBuyOrder);
        assertThat(highBuyOrderMatched, is(false));

        boolean sellOrderMatched = service.placeOrder(sellOrder);
        assertThat(sellOrderMatched, is(true));

        List<CompletedTrade> completedTrades = service.completedTrades();
        assertThat(completedTrades, notNullValue());
        assertThat(completedTrades.size(), is(1));

        CompletedTrade completedTrade = completedTrades.get(0);
        assertThat(completedTrade.getBuyOrder(), equalTo(highBuyOrder));
        assertThat(completedTrade.getSellOrder(), equalTo(sellOrder));
    }

    @Test
    public void testBuyOrderMatchesLowestSellOrder() {
        TradeOrder lowSellOrder = stubOrder(TradeType.SELL, BigDecimal.ONE);
        TradeOrder highSellOrder = stubOrder(TradeType.SELL, BigDecimal.TEN);
        TradeOrder buyOrder = stubOrder(TradeType.BUY, BigDecimal.valueOf(100));

        boolean highSellOrderMatched = service.placeOrder(highSellOrder);
        assertThat(highSellOrderMatched, is(false));

        boolean lowSellOrderMatched = service.placeOrder(lowSellOrder);
        assertThat(lowSellOrderMatched, is(false));

        boolean buyOrderMatched = service.placeOrder(buyOrder);
        assertThat(buyOrderMatched, is(true));

        List<CompletedTrade> completedTrades = service.completedTrades();
        assertThat(completedTrades, notNullValue());
        assertThat(completedTrades.size(), is(1));

        CompletedTrade completedTrade = completedTrades.get(0);
        assertThat(completedTrade.getBuyOrder(), equalTo(buyOrder));
        assertThat(completedTrade.getSellOrder(), equalTo(lowSellOrder));
    }

    @Test
    public void testOldestBuyOrderIsMatchedForSellOrder() {
        TradeOrder firstBuyOrder = stubOrder(TradeType.BUY, BigDecimal.TEN);
        TradeOrder secondBuyOrder = stubOrder(TradeType.BUY, BigDecimal.TEN);
        TradeOrder sellOrder = stubOrder(TradeType.SELL, BigDecimal.ONE);

        boolean firstBuyOrderMatched = service.placeOrder(firstBuyOrder);
        assertThat(firstBuyOrderMatched, is(false));

        boolean secondBuyOrderMatched = service.placeOrder(secondBuyOrder);
        assertThat(secondBuyOrderMatched, is(false));

        boolean sellOrderMatched = service.placeOrder(sellOrder);
        assertThat(sellOrderMatched, is(true));

        List<CompletedTrade> completedTrades = service.completedTrades();
        assertThat(completedTrades, notNullValue());
        assertThat(completedTrades.size(), is(1));

        CompletedTrade completedTrade = completedTrades.get(0);
        assertThat(completedTrade.getBuyOrder(), equalTo(firstBuyOrder));
        assertThat(completedTrade.getSellOrder(), equalTo(sellOrder));
    }

    @Test
    public void testOldestSellOrderIsMatchedForBuyOrder() {
        TradeOrder firstSellOrder = stubOrder(TradeType.SELL, BigDecimal.ONE);
        TradeOrder secondSellOrder = stubOrder(TradeType.SELL, BigDecimal.ONE);
        TradeOrder buyOrder = stubOrder(TradeType.BUY, BigDecimal.TEN);

        boolean firstSellOrderMatched = service.placeOrder(firstSellOrder);
        assertThat(firstSellOrderMatched, is(false));

        boolean secondSellOrderMatched = service.placeOrder(secondSellOrder);
        assertThat(secondSellOrderMatched, is(false));

        boolean buyOrderMatched = service.placeOrder(buyOrder);
        assertThat(buyOrderMatched, is(true));

        List<CompletedTrade> completedTrades = service.completedTrades();
        assertThat(completedTrades, notNullValue());
        assertThat(completedTrades.size(), is(1));

        CompletedTrade completedTrade = completedTrades.get(0);
        assertThat(completedTrade.getBuyOrder(), equalTo(buyOrder));
        assertThat(completedTrade.getSellOrder(), equalTo(firstSellOrder));
    }
}
