package ru.job4j.url.shortcut.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.repository.UrlRepository;
import ru.job4j.url.shortcut.repository.UrlRepositoryQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UrlServiceImplTest {
    @MockBean
    private UrlRepository urlRepository;
    @MockBean
    private UrlRepositoryQuery urlRepositoryQuery;
    @Autowired
    private UrlService urlService;

    @Test
    @WithMockUser(value = "1")
    void save() {
        ArgumentCaptor<Url> argumentCaptor = ArgumentCaptor.forClass(Url.class);
        Url url = new Url().setLink("https://220test.ru");
        urlService.save(url);
        verify(urlRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCode().length()).isEqualTo(10);
    }

    @Test
    void findByCode() {
        String code = RandomStringUtils.randomAlphabetic(10);
        urlService.findByCode(code);
        verify(urlRepositoryQuery).findByCode(code);
    }

    @Test
    @WithMockUser(value = "1", authorities = {"ROLE_ADMIN"})
    void whenRoleIsAdminThenFindAll() {
        Pageable pageable = PageRequest.of(1, 1);
        urlService.findAll(pageable);
        verify(urlRepository).findAll(pageable);
    }

    @Test
    @WithMockUser(value = "1")
    void whenRoleNotAdminThenFindByClient() {
        Pageable pageable = PageRequest.of(1, 1);
        ArgumentCaptor<Client> argumentCaptor = ArgumentCaptor.forClass(Client.class);
        urlService.findAll(pageable);
        verify(urlRepository).findAllByClient(argumentCaptor.capture(), eq(pageable));
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(1L);
    }
}