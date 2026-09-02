package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.adapter.out.persistence.mapper.CustomerEntityMapper;
import br.com.claudiocarige.desafio.application.port.out.SearchCustomersRepositoryPort;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class SearchCustomersRepository implements SearchCustomersRepositoryPort {

    private final CustomerRepository customerRepository;

    public SearchCustomersRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public SearchResult search(int page, int size) {
        log.info("### INICIANDO SearchCustomersRepository.search - Page: {}, Size: {} ###", page, size);

        Page<CustomerEntity> pageResult = customerRepository.findAllCustomersNative(PageRequest.of(page, size));
        SearchResult result = mapResult(pageResult);

        log.info("### FINALIZANDO SearchCustomersRepository.search - Total encontrados: {} ###", result.totalElements());
        return result;
    }

    @Override
    public SearchResult searchByName(String name, int page, int size) {
        log.info("### INICIANDO SearchCustomersRepository.searchByName - Nome: {}, Page: {}, Size: {} ###", name, page, size);

        String normalizedName = name.trim();
        Page<CustomerEntity> pageResult = customerRepository.findByNameContainingNative(normalizedName, PageRequest.of(page, size));
        SearchResult result = mapResult(pageResult);

        log.info("### FINALIZANDO SearchCustomersRepository.searchByName - Total encontrados: {} ###", result.totalElements());
        return result;
    }

    @Override
    public SearchResult searchByStatus(CustomerStatus status, int page, int size) {
        log.info("### INICIANDO SearchCustomersRepository.searchByStatus - Status: {}, Page: {}, Size: {} ###", status, page, size);

        Page<CustomerEntity> pageResult = customerRepository.findByStatusNative(status.name(), PageRequest.of(page, size));
        SearchResult result = mapResult(pageResult);

        log.info("### FINALIZANDO SearchCustomersRepository.searchByStatus - Total encontrados: {} ###", result.totalElements());
        return result;
    }

    private SearchResult mapResult(Page<CustomerEntity> pageResult) {
        List<Customer> customers = pageResult.stream()
                .map(CustomerEntityMapper::customerEntityToCustomer)
                .toList();

        return new SearchResult(customers, pageResult.getTotalElements());
    }
}
