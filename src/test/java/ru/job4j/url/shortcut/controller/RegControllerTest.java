package ru.job4j.url.shortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.dto.RegistrationDto;
import ru.job4j.url.shortcut.repository.ClientRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void cleanDb() {
        clientRepository.deleteAll();
    }

    @Test
    void whenUniqueClientRegistrationThenSuccess() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto().setSite("https://yandex.ru");
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(Matchers.hasLength(10)))
                .andExpect(jsonPath("$.password").value(Matchers.hasLength(10)))
                .andExpect(jsonPath("$.registration").value(Matchers.equalTo(true)));
    }

    @Test
    void whenNotUniqueClientRegistrationThenErrMsg() throws Exception {
        clientRepository.save(new Client().setSite("https://yandex.ru").setLogin("a").setPassword("b"));
        RegistrationDto registrationDto = new RegistrationDto().setSite("https://yandex.ru");
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(Matchers.nullValue(String.class)))
                .andExpect(jsonPath("$.password").value(Matchers.nullValue(String.class)))
                .andExpect(jsonPath("$.registration").value(Matchers.equalTo(false)));
    }
}