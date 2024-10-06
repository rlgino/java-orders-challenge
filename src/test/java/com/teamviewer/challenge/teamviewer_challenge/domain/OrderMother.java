package com.teamviewer.challenge.teamviewer_challenge.domain;

import java.util.UUID;

public class OrderMother {
    public static Order dummy() {
        return new Order(UUID.randomUUID());
    }
}
