package ru.job4j.url.shortcut.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.job4j.url.shortcut.mappers.UrlMapper;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.UrlDto;
import ru.job4j.url.shortcut.repository.UrlRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UrlServiceImpl.class)
class UrlServiceImplTest {
    @MockBean
    private UrlRepository urlRepository;
    @MockBean
    private UrlMapper urlMapper;
    @Autowired
    private UrlService urlService;

    @Test
    void save() {
        ArgumentCaptor<Url> argumentCaptor = ArgumentCaptor.forClass(Url.class);
        UrlDto urlDto = new UrlDto().setUrl("https://220test.ru");
        when(urlMapper.urlFromUrlDto(any(UrlDto.class))).thenReturn(new Url());
        urlService.save(urlDto);
        verify(urlRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCode().length()).isEqualTo(10);
    }

    @Test
    void findByCode() {
        String code = RandomStringUtils.randomAlphabetic(10);
        when(urlRepository.updateTotal(code)).thenReturn(1);
        urlService.findByCode(code);
        verify(urlRepository).findByCode(code);
    }

    @Test
    void whenFindAllThenInvokeFindAll() {
        Pageable pageable = PageRequest.of(1, 1);
        when(urlRepository.findAll(pageable)).thenReturn(Page.empty());
        urlService.findAll(pageable);
        verify(urlRepository).findAll(pageable);
    }
}