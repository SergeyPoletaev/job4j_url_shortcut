package ru.job4j.url.shortcut.security.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class JWTRequest {
    @NotNull
    @Size(min = 3)
    private String login;
    @NotNull
    @Size(min = 3)
    private String password;
}
