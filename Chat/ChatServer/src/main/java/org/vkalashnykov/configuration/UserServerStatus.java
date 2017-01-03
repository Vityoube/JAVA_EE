package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 02.01.17.
 */
public enum UserServerStatus {
    OK("Ok"),BAN("Ban"), CLOSE("Close");

    UserServerStatus(String userServerStatus){
        this.userServerStatus=userServerStatus;
    }

    private String userServerStatus;

    public String getUserServerStatus() {
        return userServerStatus;
    }
}
