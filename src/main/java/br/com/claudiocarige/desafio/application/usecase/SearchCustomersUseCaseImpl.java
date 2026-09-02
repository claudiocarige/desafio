package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.mapper.CustomerDtoMapper;
import br.com.claudiocarige.desafio.application.port.in.SearchCustomersUseCase;
import br.com.claudiocarige.desafio.application.port.out.SearchCustomersRepositoryPort;
import br.com.claudiocarige.desafio.application.valueobject.PageQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SearchCustomersUseCaseImpl implements SearchCustomersUseCase {

    private final SearchCustomersRepositoryPort searchCustomersRepository;

    public SearchCustomersUseCaseImpl(SearchCustomersRepositoryPort searchCustomersRepository) {
        this.searchCustomersRepository = searchCustomersRepository;
    }

    @Override
    public CustomerPageDto execute(Integer page, Integer size, String name) {
        log.info("### INICIANDO SearchCustomersUseCaseImpl - Page: {}, Size: {}, Nome: {} ###", page, size, name);

        PageQuery pageQuery = PageQuery.of(page, size);
        boolean isNameBlank = name == null || name.isBlank();

        SearchCustomersRepositoryPort.SearchResult result = isNameBlank
                ? searchCustomersRepository.search(pageQuery.page(), pageQuery.size())
                : searchCustomersRepository.searchByName(name.trim(), pageQuery.page(), pageQuery.size());

        List<CustomerDto> content = result.content().stream()
                .map(CustomerDtoMapper::customerToCustomerDto)
                .toList();

        CustomerPageDto pageDto = CustomerPageDto.of(content, pageQuery.page(), pageQuery.size(), result.totalElements());

        log.info("### FINALIZANDO SearchCustomersUseCaseImpl - Total encontrados: {} ###", pageDto.totalElements());
        return pageDto;
    }
}
