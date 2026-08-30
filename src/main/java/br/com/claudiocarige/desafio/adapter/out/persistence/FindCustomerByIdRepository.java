package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.NotFoundException;
import br.com.claudiocarige.desafio.domain.model.Customer;
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

        return CustomerMapper.customerEntityToCustomer(customerEntity);
    }
}
