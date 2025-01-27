package com.rlgino.OrdersService.order.domain.exceptions;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException() {
        super("Invalid quantity for Order Item");
    }
}
