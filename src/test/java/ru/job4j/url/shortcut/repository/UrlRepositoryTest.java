package ru.job4j.url.shortcut.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.job4j.url.shortcut.model.Url;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void cleanDb() {
        urlRepository.deleteAll();
    }

    @Test
    void findByCode() {
        String code = RandomStringUtils.randomAlphabetic(10);
        String link = "https://220test.ru";
        Url expectedUrl = new Url().setLink(link).setCode(code).setTotal(1);
        urlRepository.save(expectedUrl);
        Optional<Url> urlOpt = urlRepository.findByCode(code);
        assertThat(urlOpt.get().getLink()).isEqualTo(expectedUrl.getLink());
        assertThat(urlOpt.get().getTotal()).isEqualTo(expectedUrl.getTotal());
    }
}