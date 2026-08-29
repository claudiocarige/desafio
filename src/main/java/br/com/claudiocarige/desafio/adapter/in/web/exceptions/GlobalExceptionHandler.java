package br.com.claudiocarige.desafio.adapter.in.web.exceptions;


import br.com.claudiocarige.desafio.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        log.warn("Erro de Validação no '{}': {}", request.getRequestURI(), fieldErrors);

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiError.of(
                        422, "Erro de Validação",
                        "Um ou mais campos inválidos.",
                        request.getRequestURI(),
                        fieldErrors
                ));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(
            DomainException ex,
            HttpServletRequest request) {

        log.warn("Erro no Domínio no '{}': {}", request.getRequestURI(), ex.getMessage());

        List<ApiError.FieldError> fieldErrors = ex.getErrors()
                .stream()
                .map(e -> new ApiError.FieldError(e.property(), e.message()))
                .toList();
        var status = HttpStatus.UNPROCESSABLE_ENTITY;
        if (!fieldErrors.isEmpty()) {
            return ResponseEntity
                    .status(status)
                    .body(ApiError.of(
                            status.value(),
                            status.getReasonPhrase(),
                            ex.getMessage(),
                            request.getRequestURI()
                    ));
        }
        return ResponseEntity
                .status(status)
                .body(ApiError.of(
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        fieldErrors
                ));
    }

}
