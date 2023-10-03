package ru.job4j.url.shortcut.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.CodeDto;
import ru.job4j.url.shortcut.model.dto.StatisticDto;
import ru.job4j.url.shortcut.model.dto.UrlDto;
import ru.job4j.url.shortcut.repository.ClientRepository;
import ru.job4j.url.shortcut.repository.UrlRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UrlServiceImplIntegratedWithRepositoryTest {
    @Autowired
    private UrlServiceImpl urlService;
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void ensurePrecondition() {
        urlRepository.deleteAll();
        clientRepository.deleteAll();
        Client testClient = clientRepository.save(new Client().setSite("a").setLogin("b").setPassword("c"));
        User user = new User(testClient.getId().toString(), "null", List.of());
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void save() {
        UrlDto urlDto = new UrlDto().setUrl("https://220test.ru");
        assertThat(urlService.save(urlDto).getCode().length()).isEqualTo(10);
    }

    @Test
    void findByCode() {
        String link = "https://220test.ru";
        UrlDto urlDto = new UrlDto().setUrl(link);
        CodeDto codeDto = urlService.save(urlDto);
        Optional<Url> urlOpt = urlService.findByCode(codeDto.getCode());
        assertThat(urlOpt.get().getLink()).isEqualTo(link);
        assertThat(urlOpt.get().getTotal()).isEqualTo(1L);
    }

    @Test
    void findAll() {
        UrlDto urlDto = new UrlDto().setUrl("https://220test.ru");
        urlService.save(urlDto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<StatisticDto> page = urlService.findAll(pageable);
        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0).getUrl()).isEqualTo(urlDto.getUrl());
    }
}