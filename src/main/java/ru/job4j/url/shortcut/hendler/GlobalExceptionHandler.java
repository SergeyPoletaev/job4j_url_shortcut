package ru.job4j.url.shortcut.hendler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public void handleUniqConstraintViolation(DataIntegrityViolationException ex,
                                              HttpServletResponse res,
                                              HttpServletRequest req) throws IOException {
        if (ex.getMostSpecificCause().getClass().getName().equals("org.postgresql.util.PSQLException")
                && ((SQLException) ex.getMostSpecificCause()).getSQLState().equals("23505")) {
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            res.setContentType("application/json");
            res.getWriter().write(objectMapper.writeValueAsString(
                    Map.of(
                            "message", ex.getMostSpecificCause().getMessage(),
                            "status", res.getStatus(),
                            "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            "timestamp", LocalDateTime.now(),
                            "path", req.getRequestURI()
                    )
            ));
            log.error(ex.getMessage(), ex);
        } else {
            throw ex;
        }
    }
}
