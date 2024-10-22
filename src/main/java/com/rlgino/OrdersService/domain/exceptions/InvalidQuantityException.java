package com.rlgino.OrdersService.domain.exceptions;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException() {
        super("Invalid quantity for Order Item");
    }
}
