package org.vkalashnykov.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by vkalashnykov on 25.12.16.
 */
public enum UserRoles implements GrantedAuthority{
    ADMIN, USER;

    @Override
    public String getAuthority() {
        return name();
    }


}
