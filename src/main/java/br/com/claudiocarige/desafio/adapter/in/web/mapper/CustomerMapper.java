package br.com.claudiocarige.desafio.adapter.in.web.mapper;

import br.com.claudiocarige.desafio.adapter.in.web.dto.CreateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerResponse;
import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.Email;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static CustomerResponse toResponse(CustomerDto customerDto) {
        return new CustomerResponse(
                customerDto.id(),
                customerDto.name(),
                customerDto.cpf(),//Todo -> Create Maskara para CPF
                customerDto.email(),
                customerDto.status()
        );
    }

    public static CreateCustomerDto toCreateCustomerDto(CreateCustomerRequest request) {
        return new CreateCustomerDto(
                request.name(),
                toCpf(request.cpf()),
                toEmail(request.email())
        );
    }

    public static Cpf toCpf(String cpf) {
        return new Cpf(cpf);
    }

    public static Email toEmail(String email) {
        return new Email(email);
    }
}
