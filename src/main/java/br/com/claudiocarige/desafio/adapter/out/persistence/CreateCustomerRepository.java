package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.adapter.out.persistence.mapper.CustomerEntityMapper;
import br.com.claudiocarige.desafio.application.port.out.CreateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.springframework.stereotype.Repository;

@Repository
public class CreateCustomerRepository implements CreateCustomerRepositoryPort {

    private final CustomerRepository customerRepository;

    public CreateCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer save(Customer customer) {
        verifyIfCpfExists(customer.getCpf().value());
        CustomerEntity entity = CustomerEntityMapper.customerToCustomerEntity(customer);

        CustomerEntity savedEntity = customerRepository.save(entity);

        return CustomerEntityMapper.customerEntityToCustomer(savedEntity);
    }

    private void verifyIfCpfExists(String cpf) {
        customerRepository.findByCpf(cpf)
                .ifPresent(customerEntity -> {
                    throw DomainException.with("CPF already exists: ***.***." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11));
                });
    }
}
