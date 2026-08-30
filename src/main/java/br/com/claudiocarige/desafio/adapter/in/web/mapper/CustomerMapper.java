package br.com.claudiocarige.desafio.adapter.in.web.mapper;

import br.com.claudiocarige.desafio.adapter.in.web.dto.CreateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerPageResponse;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerResponse;
import br.com.claudiocarige.desafio.adapter.out.persistence.CustomerEntity;
import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.domain.model.Customer;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.CustomerId;
import br.com.claudiocarige.desafio.domain.valueobject.Email;
import org.springframework.data.domain.Page;

import java.util.List;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerResponse customerDtoToCustomerResponse(CustomerDto customerDto) {
        return new CustomerResponse(
                customerDto.id(),
                customerDto.name(),
                maskCpf(customerDto.cpf()),
                customerDto.email(),
                customerDto.status()
        );
    }

    public static Customer createCustomerDtoToCustomer(CreateCustomerDto createCustomerDto) {
        return Customer.create(
                createCustomerDto.name(),
                createCustomerDto.cpf(),
                createCustomerDto.email()
        );
    }

    public static CreateCustomerDto createCustomerRequestToCreateCustomerDto(CreateCustomerRequest request) {
        return new CreateCustomerDto(
                request.name(),
                toCpf(request.cpf()),
                toEmail(request.email())
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

    public static Customer customerDtoToCustomer(CustomerDto customerDto) {
        return Customer.restore(
                customerDto.id() != null ? new CustomerId(customerDto.id()) : null,
                customerDto.name(),
                new Cpf(customerDto.cpf()),
                new Email(customerDto.email()),
                customerDto.status()
        );
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

    public static CustomerPageResponse customerPageToCustomerPageResponse(List<CustomerResponse> customerResponses, CustomerPageDto customersPage) {
        CustomerPageResponse response = CustomerPageResponse.of(
                customerResponses,
                customersPage.page(),
                customersPage.size(),
                customersPage.totalElements(),
                customersPage.totalPages(),
                customersPage.hasNext(),
                customersPage.hasPrevious()
        );
        return response;
    }

    public static Cpf toCpf(String cpf) {
        return new Cpf(cpf);
    }

    public static Email toEmail(String email) {
        return new Email(email);
    }

    private static String maskCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return cpf;
        }

        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) {
            return cpf;
        }

        return "***.***.%s-%s".formatted(digits.substring(6, 9), digits.substring(9));
    }
}
