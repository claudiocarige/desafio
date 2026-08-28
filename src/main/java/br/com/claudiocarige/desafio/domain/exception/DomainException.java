package br.com.claudiocarige.desafio.domain.exception;

public class DomainException extends RuntimeException {


    protected DomainException(final String aMessage) {
        super(aMessage);
    }

    public static DomainException with(final String aMessage) {
        return new DomainException(aMessage);
    }

}

