package io.marketplace.api.order.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@XmlRootElement
public class OrderRequest {
    @NotNull(message = "{OrderRequest.item.null}")
    private OrderItem item;
    @NotNull(message = "{OrderRequest.type.null}")
    private OrderType type;
    @NotNull(message = "{OrderRequest.price.null}")
    @DecimalMin(value = "0.01", message = "{OrderRequest.price.min}")
    private BigDecimal price;
    private final LocalDateTime created = LocalDateTime.now();

    public OrderItem getItem() {
        return item;
    }

    public void setItem(OrderItem item) {
        this.item = item;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderRequest that = (OrderRequest) o;
        return item == that.item &&
                type == that.type &&
                Objects.equals(price, that.price) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, type, price, created);
    }
}
