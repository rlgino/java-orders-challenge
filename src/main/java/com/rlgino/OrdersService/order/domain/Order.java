package com.rlgino.OrdersService.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.util.*;

@Entity(name = "ORDERS")
public class Order extends AbstractAggregateRoot<Order> {
    @EmbeddedId
    private OrderID id;

    @OneToMany(targetEntity= OrderItem.class,cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private List<OrderItem> items = new ArrayList<>();

    @Column
    private BigDecimal amount;
    @Column(name="deletedAt")
    @JsonIgnore
    private Date deleteAt;

    public Order(OrderID id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.amount = BigDecimal.ZERO;
    }

    public Order() {}

    public OrderID getId() {
        return id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public Order markAsDelete() {
        final Order deletedOrder = new Order();
        deletedOrder.id = this.id;
        deletedOrder.items = this.items;
        deletedOrder.deleteAt = new Date();
        return deletedOrder;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return id.equals(order.id) && amount.setScale(2).equals(order.amount.setScale(2)) && Objects.equals(deleteAt, order.deleteAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, deleteAt);
    }
}
