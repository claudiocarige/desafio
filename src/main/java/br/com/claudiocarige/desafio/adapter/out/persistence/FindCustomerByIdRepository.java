package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.NotFoundException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.CustomerId;
import br.com.claudiocarige.desafio.domain.valueobject.Email;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class FindCustomerByIdRepository implements FindCustomerByIdRepositoryPort {

    private final CustomerRepository customerRepository;

    public FindCustomerByIdRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer findById(UUID id) {
       CustomerEntity customerEntity = customerRepository.findById(id).orElseThrow(() -> NotFoundException.of("Cliente", id));

        return toDomain(customerEntity);
    }

    private Customer toDomain(CustomerEntity entity) {
        return Customer.restore(
                new CustomerId(entity.getId()),
                entity.getName(),
                new Cpf(entity.getCpf()),
                new Email(entity.getEmail()),
                entity.getStatus()
        );
    }
}
