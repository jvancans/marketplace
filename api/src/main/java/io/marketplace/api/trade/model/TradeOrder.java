package io.marketplace.api.trade.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@XmlRootElement
public class TradeOrder {
    @NotNull(message = "{TradeOrder.product.null}")
    private TradeProduct product;
    @NotNull(message = "{TradeOrder.type.null}")
    private TradeType type;
    @NotNull(message = "{TradeOrder.price.null}")
    @DecimalMin(value = "0.01", message = "{TradeOrder.price.min}")
    private BigDecimal price;
    private final LocalDateTime created = LocalDateTime.now();

    public TradeProduct getProduct() {
        return product;
    }

    public void setProduct(TradeProduct product) {
        this.product = product;
    }

    public TradeType getType() {
        return type;
    }

    public void setType(TradeType type) {
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
        TradeOrder that = (TradeOrder) o;
        return product == that.product &&
                type == that.type &&
                Objects.equals(price, that.price) &&
                Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, type, price, created);
    }
}
