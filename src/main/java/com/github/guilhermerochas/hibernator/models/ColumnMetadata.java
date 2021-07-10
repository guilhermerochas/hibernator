package com.github.guilhermerochas.hibernator.models;

import java.util.Optional;

public class ColumnMetadata {
    private String columnName;
    private boolean isNullable;
    private Optional<Integer> characterMinimumLength;
    private String dataType;
    private boolean isPrimaryKey;
    private boolean isGenerated;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        isNullable = nullable;
    }

    public Optional<Integer> getCharacterMinimumLength() {
        return characterMinimumLength;
    }

    public void setCharacterMinimumLength(Integer characterMinimumLength) {
        if (characterMinimumLength == null) {
            this.characterMinimumLength = Optional.empty();
            return;
        }

        this.characterMinimumLength = Optional.of(characterMinimumLength);
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public void setGenerated(boolean generated) {
        isGenerated = generated;
    }
}
