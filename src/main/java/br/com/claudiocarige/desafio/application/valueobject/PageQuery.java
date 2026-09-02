package br.com.claudiocarige.desafio.application.valueobject;

import br.com.claudiocarige.desafio.domain.exception.DomainException;


public record PageQuery(int page, int size) {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    public PageQuery {
        if (page < 0) {
            throw DomainException.with("Página deve ser maior ou igual a zero");
        }
        if (size <= 0) {
            throw DomainException.with("Tamanho da página deve ser maior que zero");
        }
        if (size > MAX_SIZE) {
            throw DomainException.with("Tamanho da página não pode ser maior que " + MAX_SIZE);
        }
    }

    public static PageQuery of(Integer page, Integer size) {
        return new PageQuery(
                page != null ? page : DEFAULT_PAGE,
                size != null ? size : DEFAULT_SIZE
        );
    }
}
