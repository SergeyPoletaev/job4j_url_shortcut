package ru.job4j.url.shortcut.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.CodeDto;
import ru.job4j.url.shortcut.model.dto.StatisticDto;
import ru.job4j.url.shortcut.model.dto.UrlDto;

import java.util.Optional;

public interface UrlService {

    CodeDto save(UrlDto url);

    Optional<Url> findByCode(String code);

    Page<StatisticDto> findAll(Pageable pageable);

    int updateTotal(String code);
}
