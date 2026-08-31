package br.com.claudiocarige.desafio.application.port.out;

import br.com.claudiocarige.desafio.domain.model.Customer;

import java.util.List;

public interface SearchCustomersRepositoryPort {

    SearchResult search(int page, int size);

    SearchResult searchByName(String name, int page, int size);

    record SearchResult(List<Customer> content, long totalElements) {
    }
}
