package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.adapter.in.web.exceptions.IllegalArgumentException;
import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.port.in.SearchCustomersUseCase;
import br.com.claudiocarige.desafio.application.port.out.SearchCustomersRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCustomersUseCaseImpl implements SearchCustomersUseCase {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final SearchCustomersRepositoryPort searchCustomersRepository;

    public SearchCustomersUseCaseImpl(SearchCustomersRepositoryPort searchCustomersRepository) {
        this.searchCustomersRepository = searchCustomersRepository;
    }

    @Override
    public CustomerPageDto execute(Integer page, Integer size, String name) {
        int currentPage = page != null ? page : DEFAULT_PAGE;
        int currentSize = size != null ? size : DEFAULT_SIZE;
        boolean nameExiste = isNameExiste(name);
        if (currentPage < 0) {
            throw new IllegalArgumentException("Página deve ser maior ou igual a zero");
        }
        if (currentSize <= 0) {
            throw new IllegalArgumentException("Tamanho da página deve ser maior que zero");
        }
        if (currentSize > MAX_SIZE) {
            throw new IllegalArgumentException("Tamanho da página não pode ser maior que " + MAX_SIZE);
        }

        SearchCustomersRepositoryPort.SearchResult result = nameExiste
                ? searchCustomersRepository.search(currentPage, currentSize)
                : searchCustomersRepository.searchByName(name.trim(), currentPage, currentSize);

        List<CustomerDto> content = result.content().stream()
                .map(CustomerMapper::customerToCustomerDto)
                .toList();

        return CustomerPageDto.of(content, currentPage, currentSize, result.totalElements());
    }

    private boolean isNameExiste(String name) {
        return name == null || name.isBlank();
    }
}
