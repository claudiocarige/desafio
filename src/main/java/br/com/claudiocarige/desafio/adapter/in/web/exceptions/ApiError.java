package br.com.claudiocarige.desafio.adapter.in.web.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        @Schema(description = "Data e hora em que o erro ocorreu") LocalDateTime timestamp,
        @Schema(description = "Código HTTP do erro", example = "404") int status,
        @Schema(description = "Descrição resumida do erro HTTP", example = "Not Found") String error,
        @Schema(description = "Mensagem detalhada do erro", example = "Cliente não encontrado.") String message,
        @Schema(description = "Caminho da requisição que originou o erro", example = "/customers/search/{id}") String path,
        @Schema(description = "Lista de erros de validação por campo, quando aplicável") List<FieldError> fieldErrors
) {

    public record FieldError(
            @Schema(description = "Nome do campo inválido", example = "cpf") String field,
            @Schema(description = "Mensagem de validação", example = "CPF deve possuir 11 dígitos") String message) {}

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(LocalDateTime.now(), status, error, message, path, List.of());
    }

    public static ApiError of(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        return new ApiError(LocalDateTime.now(), status, error, message, path, fieldErrors);
    }
}
