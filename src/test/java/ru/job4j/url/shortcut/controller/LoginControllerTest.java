package ru.job4j.url.shortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.repository.ClientRepository;
import ru.job4j.url.shortcut.repository.RoleRepository;
import ru.job4j.url.shortcut.security.RoleTypes;
import ru.job4j.url.shortcut.security.model.JWTRequest;

import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void ensurePrecondition() {
        clientRepository.deleteAll();
        String roleRegNotation = RoleTypes.USER.replace("ROLE_", "").toLowerCase(Locale.ROOT);
        Client client = new Client()
                .setLogin("login")
                .setPassword(passwordEncoder.encode("password"))
                .setSite("www")
                .setRoles(List.of(roleRepository.findByName(roleRegNotation).orElseThrow()));
        clientRepository.save(client);
    }

    @Test
    void login() throws Exception {
        JWTRequest req = new JWTRequest().setLogin("login").setPassword("password");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(Matchers.notNullValue()));
    }
}