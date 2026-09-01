package br.com.claudiocarige.desafio.domain.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String entityName, Object id) {
        return new NotFoundException(
                "%s com id '%s' não foi encontrado.".formatted(entityName, id)
        );
    }
}
