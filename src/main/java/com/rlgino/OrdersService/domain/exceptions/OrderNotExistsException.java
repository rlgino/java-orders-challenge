package com.rlgino.OrdersService.domain.exceptions;

import java.util.UUID;

public class OrderNotExistsException extends RuntimeException{

    public OrderNotExistsException(UUID id){
        super(String.format("Order not found for ID %s", id));
    }
}
