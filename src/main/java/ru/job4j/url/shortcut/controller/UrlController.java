package ru.job4j.url.shortcut.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.CodeDto;
import ru.job4j.url.shortcut.model.dto.StatisticDto;
import ru.job4j.url.shortcut.model.dto.UrlDto;
import ru.job4j.url.shortcut.service.UrlService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/convert")
    public ResponseEntity<CodeDto> convert(@RequestBody @Valid UrlDto urlDto,
                                           @AuthenticationPrincipal Jwt jwt) {
        log.info("Регистрация URL ==> {} пользователем c id {}", urlDto.getUrl(), jwt.getSubject());
        return ResponseEntity.ok(urlService.save(urlDto));
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<Void> redirect(@PathVariable @Size(min = 10, max = 10) String code) {
        log.info("Запрос URL по коду ==> {}", code);
        Url url = urlService.findByCode(code).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("URL address with code [%s] not found", code)));
        if (urlService.updateTotal(code) != 1) {
            log.error("Не удалось выполнить обновление счетчика при запросе URL по коду ==> {}", code);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url.getLink()).build();
    }

    @GetMapping("/statistic")
    public ResponseEntity<Page<StatisticDto>> getStatistic(@RequestParam(value = "pageNum", defaultValue = "0")
                                                           @Min(0) int pageNum,
                                                           @RequestParam(value = "sizePage", defaultValue = "20")
                                                           @Min(1) @Max(100) int sizePage,
                                                           @AuthenticationPrincipal Jwt jwt) {
        log.info("Запрос статистики переходов от пользователя c id {}", jwt.getSubject());
        return ResponseEntity.ok(urlService.findAll(PageRequest.of(pageNum, sizePage)));
    }
}
