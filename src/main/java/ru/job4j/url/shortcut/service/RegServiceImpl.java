package ru.job4j.url.shortcut.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.job4j.url.shortcut.mappers.ClientMapper;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.dto.CredentialsDto;
import ru.job4j.url.shortcut.model.dto.RegistrationDto;
import ru.job4j.url.shortcut.repository.RegRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegServiceImpl implements RegService {
    private final RegRepository regRepository;
    private final PasswordEncoder encoder;
    private final ClientMapper urlMapper;

    @Override
    public CredentialsDto save(RegistrationDto regDto) {
        String login = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        Client client = urlMapper.clientFromRegistrationDto(regDto);
        try {
            regRepository.save(client.setLogin(login)
                            .setPassword(encoder.encode(password)))
                    .setRegistration(true);
        } catch (DataIntegrityViolationException ex) {
            log.error("попытка повторной регистрации клиента {} " + ex.getMessage(), regDto.getSite(), ex);
        }
        if (!client.isRegistration()) {
            client.setLogin(null).setPassword(null);
        } else {
            client.setLogin(login).setPassword(password);
        }
        return urlMapper.clientToCredentialsDto(client);
    }
}
