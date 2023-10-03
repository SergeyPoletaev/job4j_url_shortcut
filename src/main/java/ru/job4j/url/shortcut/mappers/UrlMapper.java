package ru.job4j.url.shortcut.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.CodeDto;
import ru.job4j.url.shortcut.model.dto.StatisticDto;
import ru.job4j.url.shortcut.model.dto.UrlDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {

    @Mapping(target = "link", source = "url")
    Url urlFromUrlDto(UrlDto urlDto);

    CodeDto urlToCodeDto(Url url);

    @Mapping(target = "url", source = "link")
    @Mapping(target = "total", source = "total")
    StatisticDto urlToStatisticDto(Url url);
}
