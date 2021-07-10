package com.github.guilhermerochas.hibernator.base;

import com.github.guilhermerochas.hibernator.models.ColumnMetadata;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface IModelBase {
    void setValidationConstraints(boolean validationConstraints);
    boolean getValidationConstraints();
    Optional<List<ColumnMetadata>> getColumnMetadata(Connection connection, String tableName);
    HashMap<String, String> getColumnFieldMap();
}
