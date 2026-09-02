package br.com.claudiocarige.desafio.adapter.out.persistence.mapper;

import br.com.claudiocarige.desafio.adapter.out.persistence.CustomerEntity;
import br.com.claudiocarige.desafio.domain.model.Customer;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.CustomerId;
import br.com.claudiocarige.desafio.domain.valueobject.Email;

public final class CustomerEntityMapper {

    private CustomerEntityMapper() {
    }

    public static CustomerEntity customerToCustomerEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        if (customer.getId() != null) {
            entity.setId(customer.getId().value());
        }
        entity.setName(customer.getName());
        entity.setCpf(customer.getCpf().value());
        entity.setEmail(customer.getEmail().value());
        entity.setStatus(customer.getStatus());
        return entity;
    }

    public static Customer customerEntityToCustomer(CustomerEntity entity) {
        return Customer.restore(
                new CustomerId(entity.getId()),
                entity.getName(),
                new Cpf(entity.getCpf()),
                new Email(entity.getEmail()),
                entity.getStatus()
        );
    }
}
