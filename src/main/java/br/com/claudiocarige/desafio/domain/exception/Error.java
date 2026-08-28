package br.com.claudiocarige.desafio.domain.exception;

public record Error(String property, String message) {

    public Error(String message) {
        this("", message);
    }
}
