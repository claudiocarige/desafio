package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.UpdateCustomerDto;
import br.com.claudiocarige.desafio.application.mapper.CustomerDtoMapper;
import br.com.claudiocarige.desafio.application.port.in.UpdateCustomerUseCase;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.application.port.out.UpdateCustomerRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
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
        log.info("### INICIANDO UpdateCustomerUseCaseImpl - ID: {} ###", id);

        if (id == null || id.toString().isBlank()) {
            log.error("XXX Error - ID do cliente não informado para atualização XXX");
            throw DomainException.with("ID do cliente é obrigatório");
        }

        Customer oldCustomer = findCustomerByIdRepository.findById(id);
        Customer updatedCustomer = CustomerDtoMapper.updateCustomerDtoToCustomer(customerDto, oldCustomer);

        Customer savedCustomer = updateCustomerRepository.update(updatedCustomer);
        CustomerDto result = CustomerDtoMapper.customerToCustomerDto(savedCustomer);

        log.info("### FINALIZANDO UpdateCustomerUseCaseImpl - ID: {} ###", result.id());
        return result;
    }
}
