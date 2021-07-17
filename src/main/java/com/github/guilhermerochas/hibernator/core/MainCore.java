package com.github.guilhermerochas.hibernator.core;

import com.github.guilhermerochas.hibernator.base.PostgresModelBase;
import com.github.guilhermerochas.hibernator.models.PropertyModel;
import com.github.guilhermerochas.hibernator.utils.PropertyUtils;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MainCore {
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpMessage = new HelpFormatter();

        Options options = new Options();

        Option tableOption = Option.builder("table")
                .hasArg()
                .argName("name")
                .desc("name of the table entity")
                .build();

        options.addOption(tableOption);
        options.addOption("h", "help", false, "prints help option");

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                helpMessage.printHelp("hibernator <opt>", options);
                return;
            }

            if (!cmd.hasOption("table")) {
                helpMessage.printHelp("hibernator <opt>", options);
                return;
            }

            System.out.println("Executing in " + System.getProperty("user.dir"));
            Properties properties = PropertyUtils.readPropertiesFromFile("./connection.properties");

            PropertyModel modelProperty = new PropertyModel()
                    .setDriver(properties.getProperty("driver"))
                    .setDatabase(properties.getProperty("database"))
                    .setHost(properties.getProperty("host"))
                    .setPassoword(properties.getProperty("password"))
                    .setUser(properties.getProperty("user"));

            String connStr = "jdbc:postgresql://" + modelProperty.getHost() + "/" + modelProperty.getDatabase() + "?user="
                    + modelProperty.getUser() + "&password=" + modelProperty.getPassoword() + "";

            try {
                Connection connection = DriverManager.getConnection(connStr);
                if (!connection.isClosed()) {
                    EntityBuilder entityBuilder = new EntityBuilder(new PostgresModelBase());
                    entityBuilder.build(connection, cmd.getOptionValue("table"));
                    connection.close();
                    return;
                }

                System.err.println("Error: Connection got closed!");
                System.exit(1);
            } catch (Exception e) {
                System.err.println("Error: Not able to establish connection to database");
                System.exit(1);
            }
        } catch (UnrecognizedOptionException e) {
            helpMessage.printHelp("hibernator <opt>", options);
        }
    }
}
