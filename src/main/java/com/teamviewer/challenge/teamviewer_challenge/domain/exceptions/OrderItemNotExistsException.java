package com.teamviewer.challenge.teamviewer_challenge.domain.exceptions;

import java.util.UUID;

public class OrderItemNotExistsException extends RuntimeException{

    public OrderItemNotExistsException(UUID id){
        super(String.format("Order item not found for ID %s", id));
    }
}
