package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
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

        Customer savedCustomer = createCustomerRepository.save(CustomerMapper.createCustomerDtoToCustomer(createCustomerDto));

        return CustomerMapper.customerToCustomerDto(savedCustomer);
    }
}
