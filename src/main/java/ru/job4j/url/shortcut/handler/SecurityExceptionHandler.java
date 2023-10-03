package ru.job4j.url.shortcut.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class SecurityExceptionHandler {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = AccessDeniedException.class)
    public void handleAccessDeniedException(Exception ex,
                                            HttpServletResponse res,
                                            HttpServletRequest req) throws IOException {
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setContentType("application/json");
        res.getWriter().write(objectMapper.writeValueAsString(
                Map.of(
                        "message", ex.getMessage(),
                        "status", res.getStatus(),
                        "error", HttpStatus.FORBIDDEN.getReasonPhrase(),
                        "timestamp", LocalDateTime.now(),
                        "path", req.getRequestURI()
                )
        ));
        log.error(ex.getMessage(), ex);
    }
}
