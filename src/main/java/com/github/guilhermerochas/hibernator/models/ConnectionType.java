package com.github.guilhermerochas.hibernator.models;

import java.util.Optional;

public enum ConnectionType {
    POSTGRES("postgres");

    private final String property;

    ConnectionType(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public static Optional<ConnectionType> getFromProperty(String property) {
        switch (property){
            case "postgres":
                return Optional.of(POSTGRES);
            default:
                return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return property;
    }
}
