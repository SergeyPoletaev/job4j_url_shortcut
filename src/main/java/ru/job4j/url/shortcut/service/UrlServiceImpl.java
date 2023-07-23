package ru.job4j.url.shortcut.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.repository.UrlRepository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UrlServiceImpl implements UrlService {
    private static final String INCREMENT_TOTAL_BY_CODE = "UPDATE url SET total = total + 1 WHERE code = :code";

    private final UrlRepository urlRepository;
    private final EntityManager em;

    @Override
    public Url save(Url url) {
        String code = RandomStringUtils.randomAlphabetic(10);
        url.setCode(code);
        return urlRepository.save(url);
    }

    @Override
    public Optional<Url> findByCode(String code) {
        return em.createNativeQuery(INCREMENT_TOTAL_BY_CODE)
                .setParameter("code", code)
                .executeUpdate() > 0
                ? urlRepository.findByCode(code) : Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Url> findAll(Pageable pageable) {
        return urlRepository.findAll(pageable);
    }
}
