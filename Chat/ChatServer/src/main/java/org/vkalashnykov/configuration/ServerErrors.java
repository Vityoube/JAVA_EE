package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 29.12.16.
 */
public enum ServerErrors {
    USER_EXISTS("The user with username already exists");

    private String userDescription;


    ServerErrors(String userDescription) {
        this.userDescription=userDescription;
    }

    public String getUserDescription(){
        return userDescription;
    }
}
