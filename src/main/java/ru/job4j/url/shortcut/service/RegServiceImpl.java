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
import ru.job4j.url.shortcut.security.RoleTypes;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegServiceImpl implements RegService {
    private final RegRepository regRepository;
    private final RoleService roleService;
    private final PasswordEncoder encoder;
    private final ClientMapper urlMapper;

    @Override
    public CredentialsDto save(RegistrationDto regDto) {
        String login = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        String roleRegNotation = RoleTypes.USER.replace("ROLE_", "").toLowerCase(Locale.ROOT);
        Client client = urlMapper.clientFromRegistrationDto(regDto);
        try {
            regRepository.save(client.setLogin(login)
                            .setPassword(encoder.encode(password))
                            .setRoles(List.of(roleService.findByName(roleRegNotation).orElseThrow())))
                    .setRegistration(true);
        } catch (NoSuchElementException ex) {
            log.error("не найдена роль {} в БД " + ex.getMessage(), client.getSite(), ex);
        } catch (DataIntegrityViolationException ex) {
            log.error("попытка повторной регистрации клиента {} " + ex.getMessage(), roleRegNotation, ex);
        }
        if (!client.isRegistration()) {
            client.setLogin(null).setPassword(null).setRoles(null);
        } else {
            client.setLogin(login).setPassword(password);
        }
        return urlMapper.clientToCredentialsDto(client);
    }
}
