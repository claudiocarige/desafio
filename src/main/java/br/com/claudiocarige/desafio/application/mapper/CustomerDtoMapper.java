package br.com.claudiocarige.desafio.application.mapper;

import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.UpdateCustomerDto;
import br.com.claudiocarige.desafio.domain.model.Customer;

public final class CustomerDtoMapper {

    private CustomerDtoMapper() {
    }

    public static Customer createCustomerDtoToCustomer(CreateCustomerDto createCustomerDto) {
        return Customer.create(
                createCustomerDto.name(),
                createCustomerDto.cpf(),
                createCustomerDto.email()
        );
    }

    public static CustomerDto customerToCustomerDto(Customer customer) {
        return new CustomerDto(
                customer.getId() != null ? customer.getId().value() : null,
                customer.getName(),
                customer.getCpf().value(),
                customer.getEmail().value(),
                customer.getStatus()
        );
    }

    public static Customer updateCustomerDtoToCustomer(UpdateCustomerDto customerDto, Customer oldCustomer) {
        return Customer.restore(
                oldCustomer.getId(),
                customerDto.name(),
                customerDto.cpf(),
                customerDto.email(),
                customerDto.status() == null ? oldCustomer.getStatus() : customerDto.status()
        );
    }
}
