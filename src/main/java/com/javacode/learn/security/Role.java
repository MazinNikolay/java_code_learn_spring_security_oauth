package com.javacode.learn.security;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, MODERATOR, ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
