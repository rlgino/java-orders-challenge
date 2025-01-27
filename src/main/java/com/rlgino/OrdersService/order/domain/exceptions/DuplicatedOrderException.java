package com.rlgino.OrdersService.order.domain.exceptions;

import com.rlgino.OrdersService.order.domain.OrderID;

import java.util.UUID;

public class DuplicatedOrderException extends RuntimeException{

    public DuplicatedOrderException(OrderID id){
        super(String.format("Duplicated order for ID %s", id));
    }
}
