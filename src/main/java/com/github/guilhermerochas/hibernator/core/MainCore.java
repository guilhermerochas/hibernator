package com.github.guilhermerochas.hibernator.core;

import com.github.guilhermerochas.hibernator.base.PostgresModelBase;
import com.github.guilhermerochas.hibernator.models.PropertyModel;
import com.github.guilhermerochas.hibernator.utils.PropertyUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MainCore {
    public static void main(String[] args) throws Exception {
        System.out.println("Executing in " + System.getProperty("user.dir"));

        Properties properties = PropertyUtils.readPropertiesFromFile("./connection.properties");

        PropertyModel modelProperty = new PropertyModel()
                .setDriver(properties.getProperty("driver"))
                .setDatabase(properties.getProperty("database"))
                .setHost(properties.getProperty("host"))
                .setPort(properties.getProperty("port"))
                .setPassoword(properties.getProperty("password"))
                .setUser(properties.getProperty("user"));

        String connStr = "jdbc:postgresql://" + modelProperty.getHost() +
                ":" + modelProperty.getPort() + "/" + modelProperty.getDatabase() + "?user="
                + modelProperty.getUser() + "&password=" + modelProperty.getPassoword() + "";


        try {
            Connection connection = DriverManager.getConnection(connStr);
            if (!connection.isClosed()) {
                EntityBuilder entityBuilder = new EntityBuilder(new PostgresModelBase());
                entityBuilder.build(connection, "contact_company", false);
                connection.close();
                return;
            }

            System.err.println("Error: Connection got closed!");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: Not able to stablish connection to database");
            System.exit(1);
        }
    }
}
