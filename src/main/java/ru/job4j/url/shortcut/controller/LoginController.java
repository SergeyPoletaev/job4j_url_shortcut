package ru.job4j.url.shortcut.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.url.shortcut.security.model.JWTRequest;
import ru.job4j.url.shortcut.security.model.JWTResponse;
import ru.job4j.url.shortcut.security.service.AuthService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<JWTResponse> login(@RequestBody @Valid JWTRequest req) {
        log.info("Попытка авторизации в системе пользователя login = {} ==> ", req.getLogin());
        JWTResponse token = authService.login(req);
        return ResponseEntity.ok(token);
    }
}
