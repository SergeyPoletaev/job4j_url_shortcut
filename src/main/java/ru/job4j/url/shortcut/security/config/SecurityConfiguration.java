package ru.job4j.url.shortcut.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.job4j.url.shortcut.service.ClientService;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/registration").permitAll()
                                .antMatchers("/redirect/**").permitAll()
                                .antMatchers("/**").authenticated())
                .csrf().disable()
                .formLogin()
                .and()
                .oauth2ResourceServer((OAuth2ResourceServerConfigurer::jwt))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(ClientService clientService) {
        return username -> clientService.findByLogin(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Пользователь %s не найден", username)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
