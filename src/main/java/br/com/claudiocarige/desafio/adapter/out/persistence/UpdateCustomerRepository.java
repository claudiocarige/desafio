package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.adapter.out.persistence.mapper.CustomerEntityMapper;
import br.com.claudiocarige.desafio.application.port.out.UpdateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.exception.NotFoundException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
public class UpdateCustomerRepository implements UpdateCustomerRepositoryPort {

    private final CustomerRepository customerRepository;

    public UpdateCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer update(Customer customer) {
        UUID customerId = customer.getId().value();
        log.info("### INICIANDO UpdateCustomerRepository - ID: {} ###", customerId);

        CustomerEntity existingEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("XXX Error - Cliente não encontrado para atualização - ID: {} XXX", customerId);
                    return NotFoundException.of("Cliente", customerId);
                });
        if (!customer.getCpf().value().equals(existingEntity.getCpf())) {
            log.error("XXX Error - Tentativa de alteração de CPF - ID: {} XXX", customerId);
            throw DomainException.with("CPF não pode ser alterado.");
        }

        existingEntity.setName(customer.getName());

        existingEntity.setEmail(customer.getEmail().value());
        existingEntity.setStatus(customer.getStatus());

        CustomerEntity updatedEntity = customerRepository.save(existingEntity);
        Customer updatedCustomer = CustomerEntityMapper.customerEntityToCustomer(updatedEntity);

        log.info("### FINALIZANDO UpdateCustomerRepository - ID: {} ###", updatedCustomer.getId().value());
        return updatedCustomer;
    }

}
