package br.com.claudiocarige.desafio.application.port.out;

import br.com.claudiocarige.desafio.domain.model.Customer;

public interface CreateCustomerRepositoryPort {

    Customer save(Customer customer);
}
