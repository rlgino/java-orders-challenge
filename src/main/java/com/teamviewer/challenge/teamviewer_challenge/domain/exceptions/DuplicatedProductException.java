package com.teamviewer.challenge.teamviewer_challenge.domain.exceptions;

import java.util.UUID;

public class DuplicatedProductException extends RuntimeException{

    public DuplicatedProductException(UUID id){
        super(String.format("Duplicated product for ID %s", id));
    }
}
