package ru.job4j.url.shortcut.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.url.shortcut.model.Url;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlRepositoryQueryImpl implements UrlRepositoryQuery {
    private static final String INCREMENT_TOTAL_BY_CODE = "UPDATE url SET total = total + 1 WHERE code = :code";
    private static final String FIND_URL_BY_CODE = "SELECT * FROM url WHERE code = :code";

    private final EntityManager em;

    @Override
    public Optional<Url> findByCode(String code) {
        return increment(code)
                ? em.createNativeQuery(FIND_URL_BY_CODE, Url.class).setParameter("code", code).getResultStream().findFirst()
                : Optional.empty();
    }

    private boolean increment(String code) {
        return em.createNativeQuery(INCREMENT_TOTAL_BY_CODE)
                .setParameter("code", code)
                .executeUpdate() > 0;
    }
}
