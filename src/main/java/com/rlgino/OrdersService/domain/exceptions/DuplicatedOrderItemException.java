package com.rlgino.OrdersService.domain.exceptions;

import java.util.UUID;

public class DuplicatedOrderItemException extends RuntimeException {
    public DuplicatedOrderItemException(UUID id) {
        super(String.format("Duplicated order for ID %s", id));
    }
}
