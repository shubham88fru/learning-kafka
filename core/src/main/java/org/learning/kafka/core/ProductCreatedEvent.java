package org.learning.kafka.core;

import java.math.BigDecimal;

public class ProductCreatedEvent {

    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    //No args constructor needed for deserialization.
    //Kafka producer will serialize this event object
    //and send to the broker. The consumer, when consuming
    //the bytes will use this same class to deserialize the bytes
    //into this object and needs to create an instance (no args constructor)
    //before populating the instance.
    public ProductCreatedEvent() {}
    public ProductCreatedEvent(String productId, String title, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
