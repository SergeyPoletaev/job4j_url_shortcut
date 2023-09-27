package ru.job4j.url.shortcut.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url.shortcut.error.AuthException;
import ru.job4j.url.shortcut.model.Client;
import ru.job4j.url.shortcut.model.Role;
import ru.job4j.url.shortcut.security.AuthorizationProperties;
import ru.job4j.url.shortcut.security.model.JWTRequest;
import ru.job4j.url.shortcut.security.model.JWTResponse;
import ru.job4j.url.shortcut.security.util.AuthHelper;
import ru.job4j.url.shortcut.service.ClientService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    public static final long EXPIRATION_TIME = 864_000_000; /* 10 days */
    public static final String TOKEN_PREFIX = "Bearer ";

    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationProperties properties;
    private Algorithm algorithm;

    @Transactional(readOnly = true)
    public JWTResponse login(JWTRequest res) {
        Client client = clientService.findByLogin(res.getLogin())
                .orElseThrow(() -> new AuthException(String.format("Пользователь %s не найден", res.getLogin())));
        boolean passChecker = passwordEncoder.matches(res.getPassword(), client.getPassword());
        if (!passChecker) {
            throw new AuthException("Введен не верный пароль");
        }
        return new JWTResponse(generateToken(client));
    }

    private String generateToken(Client client) {
        return TOKEN_PREFIX + JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withIssuer(properties.getIssuer())
                .withClaim("roles", client.getRoles().stream()
                        .map(Role::getName)
                        .toList())
                .withClaim("id", client.getId())
                .sign(algorithm);
    }

    @PostConstruct
    private void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource(properties.getPrivateKey());
        byte[] keyContentAsBytes = AuthHelper.readFromResource(resource);
        PKCS8EncodedKeySpec rsaPrivateKeySpec = new PKCS8EncodedKeySpec(keyContentAsBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey rsaPrivateKey = keyFactory.generatePrivate(rsaPrivateKeySpec);
        this.algorithm = Algorithm.RSA256((RSAPrivateKey) rsaPrivateKey);
    }

}
