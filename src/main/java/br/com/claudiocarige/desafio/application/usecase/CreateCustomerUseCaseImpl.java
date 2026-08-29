package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.port.in.CreateCustomerUseCase;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.springframework.stereotype.Service;

@Service
public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    @Override
    public CustomerDto execute(CreateCustomerDto customerDto) {
        Customer customer = Customer.create(
                customerDto.name(),
                customerDto.cpf(),
                customerDto.email()
        );

        return new CustomerDto(
                123L,
                customer.getName(),
                customer.getCpf().value(),
                customer.getEmail().value(),
                customer.getStatus() != null ? customer.getStatus() : CustomerStatus.ACTIVE
        );
    }
}
