package ru.job4j.url.shortcut.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.url.shortcut.model.dto.CredentialsDto;
import ru.job4j.url.shortcut.model.dto.RegistrationDto;
import ru.job4j.url.shortcut.service.RegService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegController {
    private final RegService regService;

    @PostMapping
    public ResponseEntity<CredentialsDto> registration(@RequestBody @Valid RegistrationDto regDto) {
        log.info("Регистрация нового пользователя ==> {} ", regDto.getSite());
        return ResponseEntity.ok(regService.save(regDto));
    }
}
