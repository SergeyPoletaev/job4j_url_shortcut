package ru.job4j.url.shortcut.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/convert")
    public ResponseEntity<CodeDto> convert(@RequestBody @Valid UrlDto urlDto) {
        Url url = new Url().setLink(urlDto.getUrl());
        urlService.save(url);
        return ResponseEntity.ok().body(new CodeDto().setCode(url.getCode()));
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<Void> redirect(@PathVariable @Size(min = 10, max = 10) String code) {
        Url url = urlService.findByCode(code).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("URL address with code [%s] not found", code)));
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url.getLink()).build();
    }

    @GetMapping("/statistic")
    public ResponseEntity<Page<StatisticDto>> getStatistic(@RequestParam(value = "pageNum", defaultValue = "0")
                                                           @Min(0) int pageNum,
                                                           @RequestParam(value = "sizePage", defaultValue = "20")
                                                           @Min(1) @Max(100) int sizePage) {
        Page<Url> page = urlService.findAll(PageRequest.of(pageNum, sizePage));
        Page<StatisticDto> statisticDtoPage = page.map(url -> new StatisticDto().setUrl(url.getLink()).setTotal(url.getTotal()));
        return ResponseEntity.ok().body(statisticDtoPage);
    }
}