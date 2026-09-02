package br.com.claudiocarige.desafio.adapter.in.web.mapper;

import br.com.claudiocarige.desafio.adapter.in.web.dto.CreateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerPageResponse;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerResponse;
import br.com.claudiocarige.desafio.adapter.in.web.dto.UpdateCustomerRequest;
import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.dto.UpdateCustomerDto;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.Email;

import java.util.List;

/**
 * Conversão entre os DTOs HTTP (request/response) e os DTOs da camada de
 * aplicação. Utilizado exclusivamente pelo {@code CustomerController}.
 */
public final class CustomerWebMapper {

    private CustomerWebMapper() {
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

    public static CreateCustomerDto createCustomerRequestToCreateCustomerDto(CreateCustomerRequest request) {
        return new CreateCustomerDto(
                request.name(),
                toCpf(request.cpf()),
                toEmail(request.email())
        );
    }

    public static UpdateCustomerDto updateCustomerRequestToUpdateCustomerDto(UpdateCustomerRequest request) {
        return new UpdateCustomerDto(
                request.name(),
                toCpf(request.cpf()),
                toEmail(request.email()),
                CustomerStatus.toEnum(request.status())
        );
    }

    public static CustomerPageResponse customerPageToCustomerPageResponse(List<CustomerResponse> customerResponses, CustomerPageDto customersPage) {
        return CustomerPageResponse.of(
                customerResponses,
                customersPage.page(),
                customersPage.size(),
                customersPage.totalElements(),
                customersPage.totalPages(),
                customersPage.hasNext(),
                customersPage.hasPrevious()
        );
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
