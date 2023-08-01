package ru.job4j.url.shortcut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.url.shortcut.model.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

}
