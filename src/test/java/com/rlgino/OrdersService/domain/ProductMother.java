package com.rlgino.OrdersService.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductMother {
    public static Product dummy() {
        return new Product(UUID.randomUUID(),"test", BigDecimal.TEN);
    }
}
