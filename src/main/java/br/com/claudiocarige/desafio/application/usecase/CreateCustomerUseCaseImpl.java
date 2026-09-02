package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.mapper.CustomerDtoMapper;
import br.com.claudiocarige.desafio.application.port.in.CreateCustomerUseCase;
import br.com.claudiocarige.desafio.application.port.out.CreateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateCustomerUseCaseImpl implements CreateCustomerUseCase {

    private final CreateCustomerRepositoryPort createCustomerRepository;

    public CreateCustomerUseCaseImpl(CreateCustomerRepositoryPort createCustomerRepository) {
        this.createCustomerRepository = createCustomerRepository;
    }

    @Override
    public CustomerDto execute(CreateCustomerDto createCustomerDto) {
        log.info("### INICIANDO CreateCustomerUseCaseImpl - Nome: {} ###", createCustomerDto.name());

        Customer savedCustomer = createCustomerRepository.save(CustomerDtoMapper.createCustomerDtoToCustomer(createCustomerDto));
        CustomerDto customerDto = CustomerDtoMapper.customerToCustomerDto(savedCustomer);

        log.info("### FINALIZANDO CreateCustomerUseCaseImpl - ID: {} ###", customerDto.id());
        return customerDto;
    }
}
