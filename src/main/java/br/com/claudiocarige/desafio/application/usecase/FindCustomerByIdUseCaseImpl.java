package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.mapper.CustomerDtoMapper;
import br.com.claudiocarige.desafio.application.port.in.FindCustomerByIdUseCase;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class FindCustomerByIdUseCaseImpl implements FindCustomerByIdUseCase {

    private final FindCustomerByIdRepositoryPort findCustomerByIdRepository;

    public FindCustomerByIdUseCaseImpl(FindCustomerByIdRepositoryPort findCustomerByIdRepository) {
        this.findCustomerByIdRepository = findCustomerByIdRepository;
    }

    @Override
    public CustomerDto findCustomerById(UUID id) {
        log.info("### INICIANDO FindCustomerByIdUseCaseImpl - ID: {} ###", id);

        if (id == null || id.toString().isBlank()) {
            log.error("XXX Error - ID do cliente não informado XXX");
            throw DomainException.with("ID do cliente é obrigatório");
        }

        Customer customer = findCustomerByIdRepository.findById(id);
        CustomerDto customerDto = CustomerDtoMapper.customerToCustomerDto(customer);

        log.info("### FINALIZANDO FindCustomerByIdUseCaseImpl - ID: {} ###", customerDto.id());
        return customerDto;
    }
}
