package ru.job4j.url.shortcut.error;

public class JWTParseException extends RuntimeException {

    public JWTParseException(String message) {
        super(message);
    }
}
