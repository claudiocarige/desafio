package br.com.claudiocarige.desafio.application.port.out;

import br.com.claudiocarige.desafio.domain.model.Customer;

public interface UpdateCustomerRepositoryPort {

    Customer update(Customer customer);
}
