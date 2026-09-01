package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.port.out.UpdateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.exception.NotFoundException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UpdateCustomerRepository implements UpdateCustomerRepositoryPort {

    private final CustomerRepository customerRepository;

    public UpdateCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer update(Customer customer) {
        UUID customerId = customer.getId().value();

        CustomerEntity existingEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> NotFoundException.of("Cliente", customerId));
        if (!customer.getCpf().value().equals(existingEntity.getCpf())) {
            throw DomainException.with("CPF não pode ser alterado.");
        }

        existingEntity.setName(customer.getName());

        existingEntity.setEmail(customer.getEmail().value());
        existingEntity.setStatus(customer.getStatus());

        CustomerEntity updatedEntity = customerRepository.save(existingEntity);
        return CustomerMapper.customerEntityToCustomer(updatedEntity);
    }

}
