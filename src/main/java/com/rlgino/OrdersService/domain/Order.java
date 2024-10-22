package com.rlgino.OrdersService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;

@Entity(name = "ORDERS")
public class Order {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToMany(targetEntity=OrderItem.class,cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private List<OrderItem> items = new ArrayList<>();

    @Column
    private BigDecimal amount;
    @Column(name="deletedAt")
    @JsonIgnore
    private Date deleteAt;

    public Order(UUID id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.amount = BigDecimal.ZERO;
    }

    public Order() {}

    public UUID getId() {
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

    public Order calculateAmount() {
       final BigDecimal amount = getItems().stream().map(i -> i.getProduct().getPrice().multiply(new BigDecimal(i.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        Order newOrder = new Order();
        newOrder.id = this.id;
        newOrder.amount = amount;
        return newOrder;
    }
}
