package ru.job4j.url.shortcut.service;

import ru.job4j.url.shortcut.model.Client;

import java.util.Optional;

public interface ClientService {

    Optional<Client> findByLogin(String login);
}
