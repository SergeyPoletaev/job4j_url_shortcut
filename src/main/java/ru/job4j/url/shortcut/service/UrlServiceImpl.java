package ru.job4j.url.shortcut.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url.shortcut.mappers.UrlMapper;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.CodeDto;
import ru.job4j.url.shortcut.model.dto.StatisticDto;
import ru.job4j.url.shortcut.model.dto.UrlDto;
import ru.job4j.url.shortcut.repository.UrlRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UrlServiceImpl implements UrlService {
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    @Override
    public CodeDto save(UrlDto urlDto) {
        String code = RandomStringUtils.randomAlphabetic(10);
        Url url = urlMapper.urlFromUrlDto(urlDto).setCode(code);
        return urlMapper.urlToCodeDto(urlRepository.save(url));
    }

    @Override
    public Optional<Url> findByCode(String code) {
        return urlRepository.updateTotal(code) > 0
                ? urlRepository.findByCode(code)
                : Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StatisticDto> findAll(Pageable pageable) {
        Page<Url> page = urlRepository.findAll(pageable);
        return page.map(urlMapper::urlToStatisticDto);
    }
}
