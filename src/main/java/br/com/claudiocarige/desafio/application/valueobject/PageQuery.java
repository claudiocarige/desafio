package br.com.claudiocarige.desafio.application.valueobject;

import br.com.claudiocarige.desafio.adapter.in.web.exceptions.IllegalArgumentException;

/**
 * Value object que representa e autovalida os parâmetros de paginação
 * (página e tamanho) utilizados pelos use cases de busca de clientes.
 * Centraliza a regra "tamanho máximo de página" para evitar duplicação
 * entre {@code SearchCustomersUseCaseImpl} e
 * {@code FindCustomersByStatusUseCaseImpl}.
 */
public record PageQuery(int page, int size) {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    public PageQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Página deve ser maior ou igual a zero");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Tamanho da página deve ser maior que zero");
        }
        if (size > MAX_SIZE) {
            throw new IllegalArgumentException("Tamanho da página não pode ser maior que " + MAX_SIZE);
        }
    }

    public static PageQuery of(Integer page, Integer size) {
        return new PageQuery(
                page != null ? page : DEFAULT_PAGE,
                size != null ? size : DEFAULT_SIZE
        );
    }
}
