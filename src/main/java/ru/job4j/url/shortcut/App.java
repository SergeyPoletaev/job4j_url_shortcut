package ru.job4j.url.shortcut;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.job4j.url.shortcut.security.AuthorizationProperties;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(AuthorizationProperties.class)
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        log.info("Rest API service job4j_url_shortcut is started ...");
    }

}
