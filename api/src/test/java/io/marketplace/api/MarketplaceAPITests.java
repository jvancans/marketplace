package io.marketplace.api;

import io.marketplace.api.trade.model.CompletedTrade;
import io.marketplace.api.trade.model.TradeOrder;
import io.marketplace.api.trade.model.TradeProduct;
import io.marketplace.api.trade.model.TradeType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MarketplaceAPITests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test0BuyOrderEnqueued() {
        TradeOrder A_clientRequest = stubOrder(TradeType.BUY, BigDecimal.TEN);
        ResponseEntity A_clientResponse = restTemplate.postForEntity("/trades", entity(A_clientRequest), ResponseEntity.class);
        assertThat(A_clientResponse.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void test1BuyOrderEnqueued() {
        TradeOrder B_clientRequest = stubOrder(TradeType.BUY, BigDecimal.valueOf(11));
        ResponseEntity B_clientResponse = restTemplate.postForEntity("/trades", entity(B_clientRequest), ResponseEntity.class);
        assertThat(B_clientResponse.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void test2SellOrderEnqueued() {
        TradeOrder C_clientRequest = stubOrder(TradeType.SELL, BigDecimal.valueOf(15));
        ResponseEntity C_clientResponse = restTemplate.postForEntity("/trades", entity(C_clientRequest), ResponseEntity.class);
        assertThat(C_clientResponse.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void test3SellOrderCompleted() {
        TradeOrder D_clientRequest = stubOrder(TradeType.SELL, BigDecimal.valueOf(9));
        ResponseEntity D_clientResponse = restTemplate.postForEntity("/trades", entity(D_clientRequest), ResponseEntity.class);
        assertThat(D_clientResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void test4CompletedOrderListContainsTradeBetweenDAndB() {
        List<CompletedTrade> allOrders = restTemplate.exchange("/trades", HttpMethod.GET, null, new ParameterizedTypeReference<List<CompletedTrade>>() {
        }).getBody();
        assertThat(allOrders, hasItem(
                allOf(
                        hasProperty("buyOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.valueOf(11))),
                                hasProperty("type", equalTo(TradeType.BUY)))
                        ),
                        hasProperty("sellOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.valueOf(9))),
                                hasProperty("type", equalTo(TradeType.SELL))
                        ))
                )
        ));
    }

    @Test
    public void test5BuyOrderEnqueued() {
        TradeOrder E_clientRequest = stubOrder(TradeType.BUY, BigDecimal.TEN);
        ResponseEntity E_clientResponse = restTemplate.postForEntity("/trades", entity(E_clientRequest), ResponseEntity.class);
        assertThat(E_clientResponse.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void test6SellOrderCompleted() {
        TradeOrder F_clientRequest = stubOrder(TradeType.SELL, BigDecimal.TEN);
        ResponseEntity F_clientResponse = restTemplate.postForEntity("/trades", entity(F_clientRequest), ResponseEntity.class);
        assertThat(F_clientResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void test7CompletedOrderListContainsTradeBetweenFAndA() {
        List<CompletedTrade> allOrders = restTemplate.exchange("/trades", HttpMethod.GET, null, new ParameterizedTypeReference<List<CompletedTrade>>() {
        }).getBody();

        assertThat(allOrders, hasItem(
                allOf(
                        hasProperty("buyOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.TEN)),
                                hasProperty("type", equalTo(TradeType.BUY)))
                        ),
                        hasProperty("sellOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.TEN)),
                                hasProperty("type", equalTo(TradeType.SELL))
                        ))
                )
        ));
    }

    @Test
    public void test8BuyOrderCompleted() {
        TradeOrder G_clientRequest = stubOrder(TradeType.BUY, BigDecimal.valueOf(100));
        ResponseEntity G_clientResponse = restTemplate.postForEntity("/trades", entity(G_clientRequest), ResponseEntity.class);
        assertThat(G_clientResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void test9CompletedOrderListContainsTradeBetweenCAndG() {
        List<CompletedTrade> allOrders = restTemplate.exchange("/trades", HttpMethod.GET, null, new ParameterizedTypeReference<List<CompletedTrade>>() {
        }).getBody();
        assertThat(allOrders, hasItem(
                allOf(
                        hasProperty("buyOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.valueOf(100))),
                                hasProperty("type", equalTo(TradeType.BUY)))
                        ),
                        hasProperty("sellOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.valueOf(15))),
                                hasProperty("type", equalTo(TradeType.SELL))
                        ))
                )
        ));
    }

    private <T> HttpEntity<T> entity(T obj) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(obj, headers);
    }

    private TradeOrder stubOrder(TradeType type, BigDecimal price) {
        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setProduct(TradeProduct.PUMPKIN);
        tradeOrder.setType(type);
        tradeOrder.setPrice(price);
        return tradeOrder;
    }
}
