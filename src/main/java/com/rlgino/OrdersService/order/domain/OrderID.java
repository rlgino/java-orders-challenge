package com.rlgino.OrdersService.order.domain;

import org.springframework.util.Assert;

import java.util.UUID;

public record OrderID(UUID value) {
    public OrderID {
        Assert.notNull(value, "id must not be null");
    }

    public String toString() {
        return value.toString();
    }
}
