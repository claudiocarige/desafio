package br.com.claudiocarige.desafio.adapter.in.web.exceptions;


import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(
            NotFoundException ex,
            HttpServletRequest request) {

        log.warn("Recurso não encontrado no '{}': {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Erro de conversão de tipo no '{}': {}", request.getRequestURI(), ex.getMessage());

        String mensagem = "O parâmetro '%s' recebeu um valor inválido. Formato esperado: %s."
                .formatted(ex.getName(), ex.getRequiredType().getSimpleName());

        var status = HttpStatus.BAD_REQUEST; // 400

        return ResponseEntity
                .status(status)
                .body(ApiError.of(
                        status.value(),
                        status.getReasonPhrase(),
                        mensagem,
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Parâmetro inválido no '{}': {}", request.getRequestURI(), ex.getMessage());

        var status = HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(ApiError.of(
                        status.value(),
                        status.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonParseError(HttpMessageNotReadableException ex,HttpServletRequest request) {

        var status = HttpStatus.BAD_REQUEST;
        String msg = "JSON inválido. Verifique se todos os campos têm valores ou remova-os.";
        log.warn("String msg = SON inválido. Verifique se todos os campos têm valores ou remova-os.; '{}': {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(status).body(
                ApiError.of(
                        status.value(),
                        status.getReasonPhrase(),
                        msg,
                        request.getRequestURI()
                ));

    }
}

