package org.vkalashnykov.configuration;

/**
 * Created by vkalashnykov on 01.01.17.
 */
public enum UserStatuses {
    ADMIN("Admininstrator"), USER("User"), VIP("VIP"), MODERATOR("Moderator");

    private String rolename;

    UserStatuses(String rolename) {
        this.rolename=rolename;
    }

    public  String getRolename(){
        return rolename;
    }
}
