package ru.job4j.url.shortcut.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.Url;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Page<Url> findAllByClient(Client client, Pageable pageable);

    @Query(value = "SELECT * FROM url WHERE code = :code",
            nativeQuery = true)
    Optional<Url> findByCode(@Param("code") String code);

    @Modifying
    @Query(value = "UPDATE url SET total = total + 1 WHERE code = :code",
            nativeQuery = true)
    int updateTotal(@Param("code") String code);
}
