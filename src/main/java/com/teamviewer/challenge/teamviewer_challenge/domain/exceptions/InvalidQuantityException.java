package com.teamviewer.challenge.teamviewer_challenge.domain.exceptions;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException() {
        super("Invalid quantity for Order Item");
    }
}
