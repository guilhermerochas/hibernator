package com.github.guilhermerochas.hibernator.models;

import java.util.Optional;

public class PropertyModel {
    private ConnectionType driver;
    private String user;
    private String passoword;
    private String database;
    private String host;


    public ConnectionType getDriver() {
        return driver;
    }

    public PropertyModel setDriver(String driverName) {
        if(ConnectionType.getFromProperty(driverName).isPresent()) {
            driver = ConnectionType.getFromProperty(driverName).get();
            return this;
        }

        System.err.println("Property `driver` not found");
        System.exit(1);
        return null;
    }

    public String getUser() {
        return user;
    }

    public PropertyModel setUser(String user) {
        this.user = setSafeProperty(user, "user").get();
        return this;
    }

    public String getPassoword() {
        return passoword;
    }

    public PropertyModel setPassoword(String passoword) {
        this.passoword = setSafeProperty(passoword, "password").get();
        return this;
    }

    public String getDatabase() {
        return database;
    }

    public PropertyModel setDatabase(String database) {
        this.database = setSafeProperty(database, "database").get();
        return this;
    }

    public String getHost() {
        return host;
    }

    public PropertyModel setHost(String host) {
        this.host = setSafeProperty(host, "host").get();
        return this;
    }

    private <T> Optional<T> setSafeProperty(T value, String propertyName) {
        try {
            if (value == null)
                throw new Exception("Property `" + propertyName +"` not found");
            return Optional.of(value);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return Optional.empty();
    }
}
