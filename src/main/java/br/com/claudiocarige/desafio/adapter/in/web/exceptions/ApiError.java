package br.com.claudiocarige.desafio.adapter.in.web.exceptions;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldError> fieldErrors
) {

    public record FieldError(String field, String message) {}

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(LocalDateTime.now(), status, error, message, path, List.of());
    }

    public static ApiError of(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        return new ApiError(LocalDateTime.now(), status, error, message, path, fieldErrors);
    }
}
