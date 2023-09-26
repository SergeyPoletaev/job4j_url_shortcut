package ru.job4j.url.shortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.CodeDto;
import ru.job4j.url.shortcut.model.dto.StatisticDto;
import ru.job4j.url.shortcut.model.dto.UrlDto;
import ru.job4j.url.shortcut.service.UrlService;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UrlController.class)
class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UrlService urlService;

    @Test
    void whenValidInputThenConvertReturns200() throws Exception {
        UrlDto urlDto = new UrlDto().setUrl("https://220test.ru");
        MvcResult mvcResult = mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        verify(urlService).save(urlCaptor.capture());
        assertThat(urlCaptor.getValue().getLink()).isEqualTo(urlDto.getUrl());
        assertThat(actualResponseBody).isEqualTo(objectMapper.writeValueAsString(new CodeDto()));
    }

    @Test
    void whenInvalidUrlThenConvertReturns400AndErrorResult() throws Exception {
        UrlDto urlDto = new UrlDto().setUrl("htt://220test.ru");
        MvcResult mvcResult = mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody =
                """
                        [{"url":"must match \\"^(https?|https)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\".
                         Actual value: htt://220test.ru"}]
                        """;
        verify(urlService, never()).save(urlCaptor.capture());
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void whenValidInputThenRedirectReturns200() throws Exception {
        Url url = new Url().setLink("https://220test.ru");
        String code = randomAlphabetic(10);
        when(urlService.findByCode(code)).thenReturn(Optional.of(url));
        mockMvc.perform(get("/redirect/{code}", code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", url.getLink()))
                .andExpect(redirectedUrl(url.getLink()));
        verify(urlService).findByCode(code);
    }

    @Test
    void whenInvalidInputThenRedirectReturns400AndErrorResult() throws Exception {
        Url url = new Url().setLink("https://220test.ru");
        String code = "QwEr";
        when(urlService.findByCode(code)).thenReturn(Optional.of(url));
        MvcResult mvcResult = mockMvc.perform(get("/redirect/{code}", code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = """
                [{"code":"size must be between 10 and 10. Actual value: QwEr"}]
                """;
        verify(urlService, never()).findByCode(code);
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void whenValidInputThenGetStatisticReturns200() throws Exception {
        String pageNum = String.valueOf(0);
        String sizePage = String.valueOf(2);
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum), Integer.parseInt(sizePage));
        Page<Url> page = new PageImpl<>(List.of(new Url().setCode(randomAlphabetic(10)).setLink("https://220test.ru")));
        when(urlService.findAll(pageable)).thenReturn(page);
        MvcResult mvcResult = mockMvc.perform(get("/statistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNum", pageNum)
                        .param("sizePage", sizePage))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        List<StatisticDto> statistics = List.of(new StatisticDto().setUrl(page.stream().findFirst().orElseThrow().getLink()));
        Page<StatisticDto> statisticDto = new PageImpl<>(statistics);
        verify(urlService).findAll(pageable);
        assertThat(actualResponseBody).isEqualTo(objectMapper.writeValueAsString(statisticDto));
    }

    @Test
    void whenInvalidInputThenGetStatisticReturns400AndErrorResult() throws Exception {
        String pageNum = String.valueOf(1);
        String sizePage = String.valueOf(101);
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum), Integer.parseInt(sizePage));
        Page<Url> page = new PageImpl<>(List.of(new Url().setCode(randomAlphabetic(10)).setLink("https://220test.ru")));
        when(urlService.findAll(pageable)).thenReturn(page);
        MvcResult mvcResult = mockMvc.perform(get("/statistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNum", pageNum)
                        .param("sizePage", sizePage))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = "[{\"sizePage\":\"must be less than or equal to 100. Actual value: 101\"}]";
        verify(urlService, never()).findAll(pageable);
        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }
}