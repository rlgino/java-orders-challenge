package com.rlgino.OrdersService.order.domain.exceptions;

import com.rlgino.OrdersService.order.domain.OrderID;

import java.util.UUID;

public class OrderNotExistsException extends RuntimeException{

    public OrderNotExistsException(OrderID id){
        super(String.format("Order not found for ID %s", id));
    }
}
