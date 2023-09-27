package ru.job4j.url.shortcut.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.url.shortcut.model.Role;
import ru.job4j.url.shortcut.repository.RoleRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;


    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
