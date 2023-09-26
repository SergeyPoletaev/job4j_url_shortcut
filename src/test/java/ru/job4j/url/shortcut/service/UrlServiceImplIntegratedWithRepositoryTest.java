package ru.job4j.url.shortcut.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url.shortcut.model.Url;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UrlServiceImplIntegratedWithRepositoryTest {
    @Autowired
    private UrlServiceImpl urlService;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void cleanDb() {
        em.createNativeQuery("DELETE FROM url").executeUpdate();
    }

    @Test
    void save() {
        Url url = new Url().setLink("https://220test.ru");
        urlService.save(url);
        assertThat(url.getId()).isNotEqualTo(0);
        assertThat(url.getCode().length()).isEqualTo(10);
    }

    @Test
    void findByCode() {
        String link = "https://220test.ru";
        Url url = new Url().setLink(link);
        urlService.save(url);
        em.clear();
        Optional<Url> urlOpt = urlService.findByCode(url.getCode());
        assertThat(urlOpt.get().getLink()).isEqualTo(link);
        assertThat(urlOpt.get().getTotal()).isEqualTo(1L);
    }

    @Test
    void findAll() {
        Url url = new Url().setLink("https://220test.ru");
        urlService.save(url);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Url> page = urlService.findAll(pageable);
        assertThat(page.getContent()).isEqualTo(Collections.singletonList(url));
    }
}