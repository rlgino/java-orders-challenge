package com.rlgino.OrdersService.domain;


import com.rlgino.OrdersService.order.domain.Order;
import com.rlgino.OrdersService.order.domain.OrderID;

import java.util.UUID;

public class OrderMother {
    public static Order dummy() {
        return new Order(new OrderID(UUID.randomUUID()));
    }
}
