package ru.job4j.url.shortcut.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleTypes {
    public static final String HIERARCHY = "ROLE_ADMIN > ROLE_USER";

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";

    public static final String ADMIN_ACCESS = "hasRole('ROLE_ADMIN')";
    public static final String USER_ACCESS = "hasRole('ROLE_USER')";

}
