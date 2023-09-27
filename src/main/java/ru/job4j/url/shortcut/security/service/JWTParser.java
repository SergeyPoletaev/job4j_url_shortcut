package ru.job4j.url.shortcut.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.job4j.url.shortcut.error.JWTParseException;
import ru.job4j.url.shortcut.security.AuthorizationProperties;
import ru.job4j.url.shortcut.security.util.AuthHelper;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JWTParser {
    private static final String TOKEN_PREFIX = "Bearer ";

    private final AuthorizationProperties properties;
    private JWTVerifier verifier;

    public DecodedJWT parseToken(String token) {
        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            throw new JWTParseException("JWT-токен должен начинаться с префикса Bearer");
        }
        DecodedJWT decodedJWT = verifier.verify(token.replaceAll(TOKEN_PREFIX, ""));
        if (getRoles(decodedJWT).isEmpty()) {
            throw new JWTParseException("JWT-токен не содержит никаких ролей");
        }
        if (getUsername(decodedJWT) == null) {
            throw new JWTParseException("JWT-токен не содержит claim id");
        }
        return decodedJWT;
    }

    public List<String> getRoles(DecodedJWT decodedJWT) {
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return roles != null ? roles : Collections.emptyList();
    }

    public String getUsername(DecodedJWT decodedJWT) {
        Long rsl = decodedJWT.getClaim("id").asLong();
        return rsl != null ? String.valueOf(rsl) : null;
    }

    @PostConstruct
    private void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource(properties.getPublicKey());
        byte[] keyContentAsBytes = AuthHelper.readFromResource(resource);
        EncodedKeySpec rsaPublicKeySpec = new X509EncodedKeySpec(keyContentAsBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey rsaPublicKey = keyFactory.generatePublic(rsaPublicKeySpec);
        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) rsaPublicKey);
        this.verifier = JWT.require(algorithm)
                .withIssuer(properties.getIssuer())
                .build();
    }
}
