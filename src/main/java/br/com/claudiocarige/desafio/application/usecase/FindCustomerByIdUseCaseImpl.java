package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.port.in.FindCustomerByIdUseCase;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindCustomerByIdUseCaseImpl implements FindCustomerByIdUseCase {

    private final FindCustomerByIdRepositoryPort findCustomerByIdRepository;

    public FindCustomerByIdUseCaseImpl(FindCustomerByIdRepositoryPort findCustomerByIdRepository) {
        this.findCustomerByIdRepository = findCustomerByIdRepository;
    }

    @Override
    public CustomerDto findCustomerById(UUID id) {
        if (id == null || id.toString().isBlank()) {
            throw DomainException.with("ID do cliente é obrigatório");
        }

        Customer customer = findCustomerByIdRepository.findById(id);

        return CustomerMapper.customerToCustomerDto(customer);
    }
}
