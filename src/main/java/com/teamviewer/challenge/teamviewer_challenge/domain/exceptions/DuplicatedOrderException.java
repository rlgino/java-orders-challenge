package com.teamviewer.challenge.teamviewer_challenge.domain.exceptions;

import java.util.UUID;

public class DuplicatedOrderException extends RuntimeException{

    public DuplicatedOrderException(UUID id){
        super(String.format("Duplicated order for ID %s", id));
    }
}
