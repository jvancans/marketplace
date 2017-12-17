package io.marketplace.api;

import io.marketplace.api.order.model.OrderItem;
import io.marketplace.api.order.model.OrderOverview;
import io.marketplace.api.order.model.OrderRequest;
import io.marketplace.api.order.model.OrderType;
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
        OrderRequest A_clientRequest = stubOrder(OrderType.BUY, BigDecimal.TEN);
        ResponseEntity A_clientResponse = restTemplate.postForEntity("/orders", entity(A_clientRequest), ResponseEntity.class);
        assertThat(A_clientResponse.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void test1BuyOrderEnqueued() {
        OrderRequest B_clientRequest = stubOrder(OrderType.BUY, BigDecimal.valueOf(11));
        ResponseEntity B_clientResponse = restTemplate.postForEntity("/orders", entity(B_clientRequest), ResponseEntity.class);
        assertThat(B_clientResponse.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void test2SellOrderEnqueued() {
        OrderRequest C_clientRequest = stubOrder(OrderType.SELL, BigDecimal.valueOf(15));
        ResponseEntity C_clientResponse = restTemplate.postForEntity("/orders", entity(C_clientRequest), ResponseEntity.class);
        assertThat(C_clientResponse.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void test3SellOrderCompleted() {
        OrderRequest D_clientRequest = stubOrder(OrderType.SELL, BigDecimal.valueOf(9));
        ResponseEntity D_clientResponse = restTemplate.postForEntity("/orders", entity(D_clientRequest), ResponseEntity.class);
        assertThat(D_clientResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void test4CompletedOrderListContainsTradeBetweenDAndB() {
        List<OrderOverview> allOrders = restTemplate.exchange("/orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<OrderOverview>>() {
        }).getBody();
        assertThat(allOrders, hasItem(
                allOf(
                        hasProperty("buyOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.valueOf(11))),
                                hasProperty("type", equalTo(OrderType.BUY)))
                        ),
                        hasProperty("sellOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.valueOf(9))),
                                hasProperty("type", equalTo(OrderType.SELL))
                        ))
                )
        ));
    }

    @Test
    public void test5BuyOrderEnqueued() {
        OrderRequest E_clientRequest = stubOrder(OrderType.BUY, BigDecimal.TEN);
        ResponseEntity E_clientResponse = restTemplate.postForEntity("/orders", entity(E_clientRequest), ResponseEntity.class);
        assertThat(E_clientResponse.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
    }

    @Test
    public void test6SellOrderCompleted() {
        OrderRequest F_clientRequest = stubOrder(OrderType.SELL, BigDecimal.TEN);
        ResponseEntity F_clientResponse = restTemplate.postForEntity("/orders", entity(F_clientRequest), ResponseEntity.class);
        assertThat(F_clientResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void test7CompletedOrderListContainsTradeBetweenFAndA() {
        List<OrderOverview> allOrders = restTemplate.exchange("/orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<OrderOverview>>() {
        }).getBody();

        assertThat(allOrders, hasItem(
                allOf(
                        hasProperty("buyOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.TEN)),
                                hasProperty("type", equalTo(OrderType.BUY)))
                        ),
                        hasProperty("sellOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.TEN)),
                                hasProperty("type", equalTo(OrderType.SELL))
                        ))
                )
        ));
    }

    @Test
    public void test8BuyOrderCompleted() {
        OrderRequest G_clientRequest = stubOrder(OrderType.BUY, BigDecimal.valueOf(100));
        ResponseEntity G_clientResponse = restTemplate.postForEntity("/orders", entity(G_clientRequest), ResponseEntity.class);
        assertThat(G_clientResponse.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void test9CompletedOrderListContainsTradeBetweenCAndG() {
        List<OrderOverview> allOrders = restTemplate.exchange("/orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<OrderOverview>>() {
        }).getBody();
        assertThat(allOrders, hasItem(
                allOf(
                        hasProperty("buyOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.valueOf(100))),
                                hasProperty("type", equalTo(OrderType.BUY)))
                        ),
                        hasProperty("sellOrder", allOf(
                                hasProperty("price", equalTo(BigDecimal.valueOf(15))),
                                hasProperty("type", equalTo(OrderType.SELL))
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

    private OrderRequest stubOrder(OrderType type, BigDecimal price) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItem(OrderItem.PUMPKIN);
        orderRequest.setType(type);
        orderRequest.setPrice(price);
        return orderRequest;
    }
}
