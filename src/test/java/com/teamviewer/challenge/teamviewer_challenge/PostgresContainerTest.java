package com.teamviewer.challenge.teamviewer_challenge;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerTest extends PostgreSQLContainer<PostgresContainerTest>{

    private static final String IMAGE_VERSION = "postgres:12.19";
    private static PostgresContainerTest container;

    private PostgresContainerTest() {
        super(IMAGE_VERSION);
    }

    public static PostgresContainerTest getInstance() {
        if (container == null) {
            container = new PostgresContainerTest();
            container.start();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}