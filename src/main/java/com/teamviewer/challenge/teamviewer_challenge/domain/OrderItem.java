package com.teamviewer.challenge.teamviewer_challenge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity(name = "ORDER_ITEM")
public class OrderItem {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column
    private int quantity;

    @OneToOne
    @JsonIgnore
    private Product product;
    @Transient
    private UUID productId;

    @Transient
    private UUID orderId;

    @Column(name="deletedAt")
    @JsonIgnore
    private Date deleteAt;

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public UUID getProductId() {
        return productId;
    }

    public UUID getOrderId() {
        return orderId;
    }


    public OrderItem delete() {
        final OrderItem oi = new OrderItem();
        oi.id = this.id;
        oi.product = this.product;
        oi.quantity = this.quantity;
        oi.deleteAt = new Date();
        return oi;
    }

    public static OrderItem createMock(){
        final OrderItem oi = new OrderItem();
        oi.id = UUID.randomUUID();
        oi.quantity = 3;
        return oi;
    }
}
