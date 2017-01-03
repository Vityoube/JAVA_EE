package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 29.12.16.
 */
public enum ErrorCodes {
    WRONG_DATE_FORMAT("Date must be in dd-mm-yyyy format"),
    EMPTY_PASSWORD_OR_USERNAME("Enter username and password"),
    USERNAME_TOO_SHORT("Username must be at least 6 symbols"),
    USERNAME_TOO_LONG("Username must be from 6 to 20 symbols"),
    PASSWORD_TOO_SHORT("Password must be at least 8 symbols"),
    PASSWORD_TOO_LONG("Password must be from 8 to 20 symbols"),
    PASSWORDS_DO_NOT_MATCH("Passwords must match"),
    SERVER_ERROR("Server error: error code {0} {1}"),
    WRONG_PASSWORD("Wrong password"),
    WRONG_NUMBER_FORMAT("The input must be a positive number"),
    EMPTY_VALUE("Required field");



    private  String errorDescription;

    ErrorCodes(String errorDescription){
        this.errorDescription=errorDescription;
    }

    public String getErrorDescription(){
        return errorDescription;
    }

}
