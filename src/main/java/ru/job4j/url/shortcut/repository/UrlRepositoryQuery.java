package ru.job4j.url.shortcut.repository;

import ru.job4j.url.shortcut.model.Url;

import java.util.Optional;

public interface UrlRepositoryQuery {

    Optional<Url> findByCode(String code);

}
