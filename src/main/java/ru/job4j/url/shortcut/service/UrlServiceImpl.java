package ru.job4j.url.shortcut.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url.shortcut.mappers.UrlMapper;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.CodeDto;
import ru.job4j.url.shortcut.model.dto.StatisticDto;
import ru.job4j.url.shortcut.model.dto.UrlDto;
import ru.job4j.url.shortcut.repository.UrlRepository;
import ru.job4j.url.shortcut.security.RoleTypes;

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
        String clientId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Url url = urlMapper.urlFromUrlDto(urlDto)
                .setCode(code)
                .setClient(new Client().setId(Long.parseLong(clientId)));
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
        User authUser = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Long clientId = Long.parseLong(authUser.getUsername());
        boolean checkAuthority = authUser.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals(RoleTypes.ADMIN));
        Page<Url> page = checkAuthority
                ? urlRepository.findAll(pageable)
                : urlRepository.findAllByClient(new Client().setId(clientId), pageable);
        return page.map(urlMapper::urlToStatisticDto);
    }
}
