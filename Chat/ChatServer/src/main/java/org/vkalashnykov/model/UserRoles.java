package org.vkalashnykov.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by vkalashnykov on 25.12.16.
 */
public enum UserRoles implements GrantedAuthority{
    ADMIN("Admininstrator"), USER("User"), VIP("VIP"), MODERATOR("Moderator");

    private String rolename;

    UserRoles(String rolename) {
        this.rolename=rolename;
    }

    @Override
    public String getAuthority() {
        return rolename;
    }


}
