package ru.job4j.url.shortcut.service;

import ru.job4j.url.shortcut.model.Role;

import java.util.Optional;

public interface RoleService {

    Optional<Role> findByName(String name);
}
