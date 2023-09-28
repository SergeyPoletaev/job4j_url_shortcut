package ru.job4j.url.shortcut.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Page<Url> findAllByClient(Client client, Pageable pageable);
}
