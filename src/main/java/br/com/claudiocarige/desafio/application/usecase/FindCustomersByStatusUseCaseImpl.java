package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.mapper.CustomerDtoMapper;
import br.com.claudiocarige.desafio.application.port.in.FindCustomersByStatusUseCase;
import br.com.claudiocarige.desafio.application.port.out.SearchCustomersRepositoryPort;
import br.com.claudiocarige.desafio.application.valueobject.PageQuery;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FindCustomersByStatusUseCaseImpl implements FindCustomersByStatusUseCase {

    private final SearchCustomersRepositoryPort searchCustomersRepository;

    public FindCustomersByStatusUseCaseImpl(SearchCustomersRepositoryPort searchCustomersRepository) {
        this.searchCustomersRepository = searchCustomersRepository;
    }

    @Override
    public CustomerPageDto execute(CustomerStatus status, Integer page, Integer size) {
        log.info("### INICIANDO FindCustomersByStatusUseCaseImpl - Status: {}, Page: {}, Size: {} ###", status, page, size);

        if (status == null) {
            log.error("XXX Error - Status não informado para busca de clientes XXX");
            throw DomainException.with("Status é obrigatório");
        }

        PageQuery pageQuery = PageQuery.of(page, size);

        SearchCustomersRepositoryPort.SearchResult result =
                searchCustomersRepository.searchByStatus(status, pageQuery.page(), pageQuery.size());

        List<CustomerDto> content = result.content().stream()
                .map(CustomerDtoMapper::customerToCustomerDto)
                .toList();

        CustomerPageDto pageDto = CustomerPageDto.of(content, pageQuery.page(), pageQuery.size(), result.totalElements());

        log.info("### FINALIZANDO FindCustomersByStatusUseCaseImpl - Total encontrados: {} ###", pageDto.totalElements());
        return pageDto;
    }
}
