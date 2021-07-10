package com.github.guilhermerochas.hibernator.base;

import com.github.guilhermerochas.hibernator.models.ColumnMetadata;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PostgresModelBase implements IModelBase {
    private Boolean validationConstraints;
    private HashMap<String, String> columnFieldMap;

    public PostgresModelBase() {
        setColumnFieldMap();
    }

    @Override
    public void setValidationConstraints(boolean validationConstraints) {
        this.validationConstraints = validationConstraints;
    }

    private void setColumnFieldMap() {
        columnFieldMap = new HashMap<String, String>() {{
            put("biginteger", "Long");
            put("bigserial", "Long");
            put("bit", "boolean");
            put("boolean", "boolean");
            put("bytea", "byte[]");
            put("character", "String");
            put("character varying", "String");
            put("date", "Date");
            put("double precision", "double");
            put("integer", "int");
            put("money", "String");
            put("numeric", "BigDecimal");
            put("real","float");
            put("smallint", "short");
            put("smallserial", "short");
            put("serial", "int");
            put("time", "Time");
            put("timestamp", "Timestamp");
            put("uuid", "UUID");
            put("xml", "SQLXML");
        }};
    }

    @Override
    public boolean getValidationConstraints() {
        if(validationConstraints == null)
            return false;
        return validationConstraints;
    }

    @Override
    public Optional<List<ColumnMetadata>> getColumnMetadata(Connection connection, String tableName) {
        try (Statement statement = connection.createStatement()) {
            ArrayList<ColumnMetadata> columnMetadataList = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(getQueryMetadata(tableName));

            while (resultSet.next()) {
                columnMetadataList.add(new ColumnMetadata() {{
                    setColumnName(resultSet.getString("column_name"));
                    setNullable(resultSet.getBoolean("is_nullable"));
                    setCharacterMinimumLength(Optional.of(resultSet.getInt("character_maximum_length")).orElse(null));
                    setDataType(resultSet.getString("data_type"));
                    setGenerated(resultSet.getBoolean("is_generated"));
                    setPrimaryKey(resultSet.getBoolean("is_primary_key"));
                }});
            }

            return Optional.of(columnMetadataList);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public HashMap<String, String> getColumnFieldMap() {
        return columnFieldMap;
    }

    private String getQueryMetadata(String tableName) {
        return "select \n" +
                "\tc.column_name,\n" +
                "   case when c.is_nullable = 'YES'\n" +
                "    \tthen true\n" +
                "        else false\n" +
                "    end as is_nullable,\n" +
                "    c.character_maximum_length,\n" +
                "    c.data_type,\n" +
                "   \tcase when c.column_default = 'nextval%'\n" +
                "    \tthen true\n" +
                "        else false\n" +
                "    end as is_generated,\n" +
                "    case when pm.column_name is NULL\n" +
                "    \tthen false\n" +
                "        else true\n" +
                "    end as is_primary_key\n" +
                "from information_schema.columns c\n" +
                "left join (select a.attname as column_name\n" +
                "    from pg_index i\n" +
                "    join pg_attribute a on a.attrelid = i.indrelid\n" +
                "    \tand a.attnum = any(i.indkey)\n" +
                "    where i.indrelid = '" + tableName + "'::regclass\n" +
                "    and i.indisprimary) \n" +
                "as pm on pm.column_name = c.column_name\n" +
                "where c.table_schema not in ('pg_catalog', 'information_schema')\n" +
                "and c.table_name = '" + tableName + "'" +
                "order by c.ordinal_position\n";
    }
}
