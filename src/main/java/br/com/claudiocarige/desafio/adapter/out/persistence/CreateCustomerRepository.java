package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.application.port.out.CreateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.CustomerId;
import br.com.claudiocarige.desafio.domain.valueobject.Email;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CreateCustomerRepository implements CreateCustomerRepositoryPort {

    private final CustomerRepository customerRepository;

    public CreateCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer save(Customer customer) {
        verifyIfCpfExists(customer.getCpf().value());
        CustomerEntity entity = new CustomerEntity();
        entity.setName(customer.getName());
        entity.setCpf(customer.getCpf().value());
        entity.setEmail(customer.getEmail().value());
        entity.setStatus(customer.getStatus());



        CustomerEntity savedEntity = customerRepository.save(entity);

        return Customer.restore(
                new CustomerId(savedEntity.getId()),
                savedEntity.getName(),
                new Cpf(savedEntity.getCpf()),
                new Email(savedEntity.getEmail()),
                savedEntity.getStatus()
        );
    }

    private void verifyIfCpfExists(String cpf) {
        customerRepository.findByCpf(cpf)
                .ifPresent(customerEntity -> {
                    throw DomainException.with("CPF already exists: ***.***.*" + cpf.substring(7, 9) + "-" + cpf.substring(9, 11));
                });

    }
}
