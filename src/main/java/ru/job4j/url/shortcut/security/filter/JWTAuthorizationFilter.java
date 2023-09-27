package ru.job4j.url.shortcut.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.job4j.url.shortcut.security.RoleTypes;
import ru.job4j.url.shortcut.security.service.JWTParser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final JWTParser jwtParser;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {
        String token = (request.getHeader(HttpHeaders.AUTHORIZATION));
        if (token != null) {
            DecodedJWT decodedJWT = jwtParser.parseToken(token);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                List<SimpleGrantedAuthority> authorities = jwtParser.getRoles(decodedJWT).stream()
                        .map(this::toInternalRole)
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                User user = new User(jwtParser.getUsername(decodedJWT), "null", authorities);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String toInternalRole(String role) {
        return switch (role) {
            case "admin" -> RoleTypes.ADMIN;
            default -> RoleTypes.USER;
        };
    }
}
