package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 01.01.17.
 */
public enum LastActions {
    REGISTRATION("Registration"), BLOCK("Block"), CLOSE("Close");

    LastActions(String actionName){
        this.actionName=actionName;
    }

    private String actionName;

    public String getActionName() {
        return actionName;
    }
}
