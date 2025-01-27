package com.rlgino.OrdersService.order.domain;

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

    @Transient
    private OrderID orderId;

    @Column(name="deletedAt")
    @JsonIgnore
    private Date deleteAt;

    public UUID getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public OrderID getOrderId() {
        return orderId;
    }


    public OrderItem delete() {
        final OrderItem oi = new OrderItem();
        oi.id = this.id;
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
