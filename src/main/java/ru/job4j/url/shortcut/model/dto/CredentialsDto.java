package ru.job4j.url.shortcut.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialsDto {
    private String login;
    private String password;
    private boolean registration;
}
