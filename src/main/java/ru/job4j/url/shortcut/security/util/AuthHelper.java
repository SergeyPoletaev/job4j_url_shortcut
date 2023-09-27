package ru.job4j.url.shortcut.security.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class AuthHelper {

    public byte[] readFromResource(ClassPathResource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream(512)) {
                byte[] buffer = new byte[4096];
                int n;
                while (-1 != (n = is.read(buffer))) {
                    os.write(buffer, 0, n);
                }
                return os.toByteArray();
            }
        }
    }
}
