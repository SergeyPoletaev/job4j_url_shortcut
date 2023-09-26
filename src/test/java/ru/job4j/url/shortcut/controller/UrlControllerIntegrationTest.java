package ru.job4j.url.shortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url.shortcut.model.Url;
import ru.job4j.url.shortcut.model.dto.StatisticDto;
import ru.job4j.url.shortcut.model.dto.UrlDto;
import ru.job4j.url.shortcut.service.UrlService;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UrlControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UrlService urlService;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void cleanDb() {
        em.createNativeQuery("DELETE FROM url").executeUpdate();
    }

    @Test
    void whenValidInputThenConvertReturns200() throws Exception {
        UrlDto urlDto = new UrlDto().setUrl("https://220test.ru");
        mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isString())
                .andExpect(jsonPath("$.code").value(Matchers.hasLength(10)))
                .andReturn();
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
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody =
                """
                        [{"url":"must match \\"^(https?|https)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]\\".
                         Actual value: htt://220test.ru"}]
                        """;
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void whenValidInputThenRedirectReturns200() throws Exception {
        Url url = new Url().setLink("https://220test.ru");
        urlService.save(url);
        mockMvc.perform(get("/redirect/{code}", url.getCode())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", url.getLink()))
                .andExpect(redirectedUrl(url.getLink()));
    }

    @Test
    void whenInvalidInputThenRedirectReturns400AndErrorResult() throws Exception {
        String code = "QwEr";
        MvcResult mvcResult = mockMvc.perform(get("/redirect/{code}", code)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = """
                [{"code":"size must be between 10 and 10. Actual value: QwEr"}]
                """;
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    void whenValidInputThenGetStatisticReturns200() throws Exception {
        Url url = new Url().setLink("https://220test.ru");
        Url url1 = new Url().setLink("https://221test.ru");
        urlService.save(url);
        urlService.save(url1);
        urlService.findByCode(url.getCode());
        em.clear();

        String pageNum = String.valueOf(0);
        String sizePage = String.valueOf(2);
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum), Integer.parseInt(sizePage));
        MvcResult mvcResult = mockMvc.perform(get("/statistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNum", pageNum)
                        .param("sizePage", sizePage))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        List<StatisticDto> statistics = List.of(
                new StatisticDto().setUrl(url.getLink()).setTotal(1),
                new StatisticDto().setUrl(url1.getLink()).setTotal(0)
        );
        Page<StatisticDto> statisticDto = new PageImpl<>(statistics, pageable, 2);
        assertThat(actualResponseBody).isEqualTo(objectMapper.writeValueAsString(statisticDto));
    }

    @Test
    void whenInvalidInputThenGetStatisticReturns400AndErrorResult() throws Exception {
        String pageNum = String.valueOf(1);
        String sizePage = String.valueOf(101);
        MvcResult mvcResult = mockMvc.perform(get("/statistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNum", pageNum)
                        .param("sizePage", sizePage))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        String expectedResponseBody = "[{\"sizePage\":\"must be less than or equal to 100. Actual value: 101\"}]";
        assertThat(actualResponseBody).isEqualTo(expectedResponseBody);
    }
}