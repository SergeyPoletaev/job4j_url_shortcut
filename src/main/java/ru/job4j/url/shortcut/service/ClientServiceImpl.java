package ru.job4j.url.shortcut.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.repository.ClientRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Override
    public Optional<Client> findByLogin(String login) {
        return clientRepository.findByLogin(login);
    }
}
