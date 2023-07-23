package ru.job4j.url.shortcut.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.job4j.url.shortcut.model.Url;

import java.util.Optional;

public interface UrlService {

    Url save(Url url);

    Optional<Url> findByCode(String code);

    Page<Url> findAll(Pageable pageable);
}
