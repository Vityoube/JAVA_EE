package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 29.12.16.
 */
public enum ServerErrors {
    USER_EXISTS("The user with that name already exists"),
    PASSWORD_TOO_SHORT("Password must be at least 8 symbols"),
    PASSWORD_TOO_LONG("Password must be from 8 to 20 symbols"),
    PASSWORDS_DO_NOT_MATCH("Passwords must match"),
    WRONG_PASSWORD("Wrong password"),
    USER_BANNED("Your account was blocked. Try login later."),
    USER_ONLINE("Looks like user with username is already logined"),
    WRONG_NUMBER_FORMAT("The input must be a positive number"),
    WRONG_CREDENTIALS("Wrong username or password");


    private String errorDescrition;


    ServerErrors(String errorDescrition) {
        this.errorDescrition=errorDescrition;

    }



    public String getErrorDescrition(){
        return errorDescrition;
    }
}
