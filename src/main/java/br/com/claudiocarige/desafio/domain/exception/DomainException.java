package br.com.claudiocarige.desafio.domain.exception;

import java.util.List;

public class DomainException extends RuntimeException {

    protected final List<Error> errors;

    protected DomainException(final String aMessage, List<Error> errors) {
        super(aMessage);
        this.errors = errors;
    }

    public static DomainException with(final String aMessage) {
        return new DomainException(aMessage, List.of(new Error(aMessage)));
    }

    public List<Error> getErrors() {
        return errors;
    }

}

