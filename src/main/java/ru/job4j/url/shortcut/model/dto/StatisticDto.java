package ru.job4j.url.shortcut.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticDto {
    private String url;
    private long total;
}
