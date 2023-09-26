package ru.job4j.url.shortcut.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url.shortcut.model.Url;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UrlRepositoryQueryImplTest {
    private static final String INSERT_INTO_TEST_DATA = "INSERT INTO url (link, code) VALUES (:link, :code)";

    @Autowired
    private UrlRepositoryQuery urlRepositoryQuery;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void cleanDb() {
        em.createNativeQuery("DELETE FROM url").executeUpdate();
    }

    @Test
    void findByCode() {
        String code = RandomStringUtils.randomAlphabetic(10);
        String link = "https://220test.ru";
        Url expectedUrl = new Url().setLink(link).setCode(code).setTotal(1);
        em.createNativeQuery(INSERT_INTO_TEST_DATA)
                .setParameter("code", code)
                .setParameter("link", link)
                .executeUpdate();
        em.clear();
        Optional<Url> urlOpt = urlRepositoryQuery.findByCode(code);
        assertThat(urlOpt.get().getLink()).isEqualTo(expectedUrl.getLink());
        assertThat(urlOpt.get().getTotal()).isEqualTo(expectedUrl.getTotal());
    }
}