package com.rlgino.OrdersService.catalog.domain;

import java.util.UUID;
import org.springframework.util.Assert;

public record ProductID(UUID value) {
    public ProductID {
        Assert.notNull(value, "Product ID can not be null");
    }
}
