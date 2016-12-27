package org.vkalashnykov.model;

/**
 * Created by vkalashnykov on 25.12.16.
 */
public enum UserField {
    USER_NAME("username");

    UserField(String field) {
        this.field = field;
    }

    private final String field;

    public String getField() {
        return field;
    }
}
