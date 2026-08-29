package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.port.in.CreateCustomerUseCase;
import br.com.claudiocarige.desafio.application.port.out.CreateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.springframework.stereotype.Service;

@Service
public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    private final CreateCustomerRepositoryPort createCustomerRepository;

    public CreateCustomerUseCaseImpl(CreateCustomerRepositoryPort createCustomerRepository) {
        this.createCustomerRepository = createCustomerRepository;
    }

    @Override
    public CustomerDto execute(CreateCustomerDto createCustomerDto) {
        Customer customer = Customer.create(
                createCustomerDto.name(),
                createCustomerDto.cpf(),
                createCustomerDto.email()
        );

        Customer savedCustomer = createCustomerRepository.save(customer);

        return new CustomerDto(
                savedCustomer.getId().value(),
                savedCustomer.getName(),
                savedCustomer.getCpf().value(),
                savedCustomer.getEmail().value(),
                savedCustomer.getStatus()
        );
    }
}
