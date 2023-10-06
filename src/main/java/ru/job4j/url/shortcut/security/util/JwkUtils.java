package ru.job4j.url.shortcut.security.util;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.experimental.UtilityClass;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@UtilityClass
public class JwkUtils {

    public RSAKey generateRSA() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateCrtKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private KeyPair generateRsaKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
