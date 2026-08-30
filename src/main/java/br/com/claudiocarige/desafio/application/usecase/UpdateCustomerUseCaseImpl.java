package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.UpdateCustomerDto;
import br.com.claudiocarige.desafio.application.port.in.UpdateCustomerUseCase;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.application.port.out.UpdateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    private final FindCustomerByIdRepositoryPort findCustomerByIdRepository;
    private final UpdateCustomerRepositoryPort updateCustomerRepository;

    public UpdateCustomerUseCaseImpl(
            FindCustomerByIdRepositoryPort findCustomerByIdRepository,
            UpdateCustomerRepositoryPort updateCustomerRepository) {
        this.findCustomerByIdRepository = findCustomerByIdRepository;
        this.updateCustomerRepository = updateCustomerRepository;
    }

    @Override
    public CustomerDto execute(UUID id, UpdateCustomerDto customerDto) {
        if (id == null || id.toString().isBlank()) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }

        Customer oldCustomer = findCustomerByIdRepository.findById(id);
        Customer updatedCustomer = CustomerMapper.updateCustomerDtoToCustomer(customerDto, oldCustomer);

        Customer savedCustomer = updateCustomerRepository.update(updatedCustomer);
        return CustomerMapper.customerToCustomerDto(savedCustomer);
    }
}
