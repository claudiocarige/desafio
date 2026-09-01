package br.com.claudiocarige.desafio.application.port.out;

import br.com.claudiocarige.desafio.domain.model.Customer;

import java.util.UUID;

public interface FindCustomerByIdRepositoryPort {

    Customer findById(UUID id);
}
