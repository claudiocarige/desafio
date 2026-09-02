package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.mapper.CustomerDtoMapper;
import br.com.claudiocarige.desafio.application.port.in.FindCustomersByStatusUseCase;
import br.com.claudiocarige.desafio.application.port.out.SearchCustomersRepositoryPort;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindCustomersByStatusUseCaseImpl implements FindCustomersByStatusUseCase {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final SearchCustomersRepositoryPort searchCustomersRepository;

    public FindCustomersByStatusUseCaseImpl(SearchCustomersRepositoryPort searchCustomersRepository) {
        this.searchCustomersRepository = searchCustomersRepository;
    }

    @Override
    public CustomerPageDto execute(CustomerStatus status, Integer page, Integer size) {
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }

        int currentPage = page != null ? page : DEFAULT_PAGE;
        int currentSize = size != null ? size : DEFAULT_SIZE;

        if (currentPage < 0) {
            throw new IllegalArgumentException("Página deve ser maior ou igual a zero");
        }
        if (currentSize <= 0) {
            throw new IllegalArgumentException("Tamanho da página deve ser maior que zero");
        }
        if (currentSize > MAX_SIZE) {
            throw new IllegalArgumentException("Tamanho da página não pode ser maior que " + MAX_SIZE);
        }

        SearchCustomersRepositoryPort.SearchResult result =
                searchCustomersRepository.searchByStatus(status, currentPage, currentSize);

        List<CustomerDto> content = result.content().stream()
                .map(CustomerDtoMapper::customerToCustomerDto)
                .toList();

        return CustomerPageDto.of(content, currentPage, currentSize, result.totalElements());
    }
}
