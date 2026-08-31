package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.port.out.SearchCustomersRepositoryPort;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchCustomersRepository implements SearchCustomersRepositoryPort {

    private final CustomerRepository customerRepository;

    public SearchCustomersRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public SearchResult search(int page, int size) {
        Page<CustomerEntity> pageResult = customerRepository.findAllCustomersNative(PageRequest.of(page, size));
        return mapResult(pageResult);
    }

    @Override
    public SearchResult searchByName(String name, int page, int size) {
        String normalizedName = name.trim();
        Page<CustomerEntity> pageResult = customerRepository.findByNameContainingNative(normalizedName, PageRequest.of(page, size));
        return mapResult(pageResult);
    }

    @Override
    public SearchResult searchByStatus(CustomerStatus status, int page, int size) {
        Page<CustomerEntity> pageResult = customerRepository.findByStatusNative(status.name(), PageRequest.of(page, size));
        return mapResult(pageResult);
    }

    private SearchResult mapResult(Page<CustomerEntity> pageResult) {
        List<Customer> customers = pageResult.stream()
                .map(CustomerMapper::customerEntityToCustomer)
                .toList();

        return new SearchResult(customers, pageResult.getTotalElements());
    }
}
