package ru.job4j.url.shortcut.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "token")
public class AuthorizationProperties {
    private String issuer;
    private String publicKey;
    private String privateKey;
}
