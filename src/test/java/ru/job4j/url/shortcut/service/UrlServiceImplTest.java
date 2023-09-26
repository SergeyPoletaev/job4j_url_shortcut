package ru.job4j.url.shortcut.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.repository.UrlRepository;
import ru.job4j.url.shortcut.repository.UrlRepositoryQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlRepositoryQuery urlRepositoryQuery;
    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
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
    void findAll() {
        Pageable pageable = PageRequest.of(1, 1);
        urlService.findAll(pageable);
        verify(urlRepository).findAll(pageable);
    }
}