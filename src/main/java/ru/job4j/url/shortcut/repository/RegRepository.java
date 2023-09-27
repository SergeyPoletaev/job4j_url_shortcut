package ru.job4j.url.shortcut.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.url.shortcut.model.Client;

public interface RegRepository extends CrudRepository<Client, Long> {

}
