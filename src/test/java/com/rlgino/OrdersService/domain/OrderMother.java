package com.rlgino.OrdersService.domain;

import java.util.UUID;

public class OrderMother {
    public static Order dummy() {
        return new Order(UUID.randomUUID());
    }
}
