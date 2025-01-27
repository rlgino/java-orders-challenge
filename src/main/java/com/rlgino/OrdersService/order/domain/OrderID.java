package com.rlgino.OrdersService.order.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.Assert;

import java.util.UUID;

public record OrderID(UUID value) {
    @JsonCreator
    public OrderID(String value) {
        this(UUID.fromString(value));
    }

    public OrderID {
        Assert.notNull(value, "id must not be null");
    }

    @JsonValue
    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
