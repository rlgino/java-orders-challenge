package com.rlgino.OrdersService.order.domain.exceptions;

import java.util.UUID;

public class ProductNotExistsException extends RuntimeException{

    public ProductNotExistsException(UUID id){
        super(String.format("Product not found for ID %s", id));
    }
}
