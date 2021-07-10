package com.github.guilhermerochas.hibernator.core;

import com.github.guilhermerochas.hibernator.base.IModelBase;
import com.github.guilhermerochas.hibernator.models.ColumnMetadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class EntityBuilder {
    IModelBase modelBase;
    StringBuilder stringFileBuilder;

    public EntityBuilder(IModelBase modelBase) {
        this.modelBase = modelBase;
    }

    public void build(Connection conn, String tableName, boolean validationConstraints) {
        try {
            if (conn.isClosed()) {
                System.err.println("Connection is closed??????");
                return;
            }

            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            if (!resultSet.next())
                throw new Exception("Not able to find specified database!");

            stringFileBuilder = new StringBuilder();
            setImports(validationConstraints);

            // Setup Default Annotations;
            stringFileBuilder.append("@Entity\n");
            stringFileBuilder.append("@Table(name = \"").append(tableName).append("\")\n");

            // Class Name
            stringFileBuilder.append("public class ").append(tableNameToClassName(tableName)).append(" {\n");

            // Column Fields
            ArrayList<ColumnMetadata> metadata = (ArrayList<ColumnMetadata>) modelBase.getColumnMetadata(conn, tableName).orElse(null);
            setColumnFields(validationConstraints, metadata);

            // Getters and Setters
            setGettersAndSetters(metadata);

            // EOF (End of the file)
            stringFileBuilder.append("}");
            System.out.println(stringFileBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void setImports(boolean validationConstraints) {
        stringFileBuilder.append("import javax.persistence.*;\n\n");
        if (validationConstraints) {
            stringFileBuilder.append("import javax.validation.constraints.NotNull;\n" +
                    "import javax.validation.constraints.Null;\n" +
                    "import javax.validation.constraints.Size;\n\n");
        }
    }

    private String tableNameToClassName(String tableName) {
        StringBuilder builder = new StringBuilder(tableName);
        builder.replace(0, 1, String.valueOf(tableName.charAt(0)).toUpperCase());

        int occurrenceIndex = 0;
        while (true) {
            occurrenceIndex = builder.indexOf("_");
            if (occurrenceIndex == -1)
                break;
            builder.replace(occurrenceIndex, occurrenceIndex + 2, String.valueOf(builder.charAt(occurrenceIndex + 1)).toUpperCase());
        }

        return builder.toString();
    }

    private String columnNameToField(String columnName) {
        StringBuilder builder = new StringBuilder(columnName);
        builder.replace(0, 1, String.valueOf(columnName.charAt(0)).toLowerCase());

        int occurrenceIndex = 0;
        while (true) {
            occurrenceIndex = builder.indexOf("_");
            if (occurrenceIndex == -1)
                break;
            builder.replace(occurrenceIndex, occurrenceIndex + 2, String.valueOf(builder.charAt(occurrenceIndex + 1)).toUpperCase());
        }

        return builder.toString();
    }

    private void setColumnFields(boolean validationConstraints, ArrayList<ColumnMetadata> metadata) {
        if (metadata == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("No columns were found in the table, wanna continue (Y/N)");
            String userInput = scanner.next();
            if (!userInput.toUpperCase().equals("Y")) {
                System.out.println("Processing Cancelled!");
                stringFileBuilder = null;
                return;
            }

            metadata = new ArrayList<>();
        }

        HashMap<String, String> columnFieldMap = modelBase.getColumnFieldMap();

        for (ColumnMetadata data : metadata) {
            if (data.isPrimaryKey()) {
                stringFileBuilder.append("@Id\n");
            }

            stringFileBuilder.append("@Column(name = \"").append(data.getColumnName()).append("\")\n");

            if (validationConstraints) {
                if (data.getCharacterMinimumLength().isPresent()) {
                    stringFileBuilder.append("@Size(min = 0, max = ").append(data.getCharacterMinimumLength().get()).append(")\n");
                }

                if (data.isNullable()) {
                    stringFileBuilder.append("@Null\n");
                } else {
                    stringFileBuilder.append("@NotNull\n");
                }
            }

            if (data.isGenerated()) {
                stringFileBuilder.append("@GeneratedValue(strategy = GenerationType.IDENTITY)\n");
            }

            stringFileBuilder.append("private ").append(columnFieldMap.getOrDefault(data.getDataType(), "NULL")).append(" ").append(columnNameToField(data.getColumnName())).append("; \n");
            stringFileBuilder.append("\n");
        }
    }

    private void setGettersAndSetters(ArrayList<ColumnMetadata> metadata) {
        HashMap<String, String> columnFieldMap = modelBase.getColumnFieldMap();

        String dataType;
        String propertyName;
        String methodName;

        for (ColumnMetadata data : metadata) {
            dataType = columnFieldMap.getOrDefault(data.getDataType(), "NULL");
            propertyName = columnNameToField(data.getColumnName());
            methodName = tableNameToClassName(data.getColumnName());

            stringFileBuilder.append("public ").append(dataType).append(" get").append(methodName).append("() {\n");
            stringFileBuilder.append("return ").append(propertyName).append("; \n");
            stringFileBuilder.append("}\n\n");

            stringFileBuilder.append("public ").append(dataType).append(" set").append(methodName).append("(").append(dataType).append(" ").append(propertyName).append(") {\n");
            stringFileBuilder.append("this.").append(propertyName).append(" = ").append(propertyName).append("; \n");
            stringFileBuilder.append("}\n\n");
        }
    }
}
