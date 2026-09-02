package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.adapter.out.persistence.mapper.CustomerEntityMapper;
import br.com.claudiocarige.desafio.application.port.out.CreateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CreateCustomerRepository implements CreateCustomerRepositoryPort {

    private final CustomerRepository customerRepository;

    public CreateCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer save(Customer customer) {
        log.info("### INICIANDO CreateCustomerRepository - Nome: {} ###", customer.getName());

        verifyIfCpfExists(customer.getCpf().value());
        CustomerEntity entity = CustomerEntityMapper.customerToCustomerEntity(customer);

        CustomerEntity savedEntity = customerRepository.save(entity);
        Customer savedCustomer = CustomerEntityMapper.customerEntityToCustomer(savedEntity);

        log.info("### FINALIZANDO CreateCustomerRepository - ID: {} ###", savedCustomer.getId().value());
        return savedCustomer;
    }

    private void verifyIfCpfExists(String cpf) {
        customerRepository.findByCpf(cpf)
                .ifPresent(customerEntity -> {
                    log.error("XXX Error - CPF já cadastrado XXX");
                    throw DomainException.with("CPF already exists: ***.***." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11));
                });
    }
}
