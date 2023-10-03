package ru.job4j.url.shortcut.service;

import ru.job4j.url.shortcut.model.dto.CredentialsDto;
import ru.job4j.url.shortcut.model.dto.RegistrationDto;

public interface RegService {

    CredentialsDto save(RegistrationDto registrationDto);
}
