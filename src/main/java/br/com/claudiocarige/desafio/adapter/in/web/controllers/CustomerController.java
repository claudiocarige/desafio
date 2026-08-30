package br.com.claudiocarige.desafio.adapter.in.web.controllers;

import br.com.claudiocarige.desafio.adapter.in.web.dto.CreateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerPageResponse;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerResponse;
import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.port.in.CreateCustomerUseCase;
import br.com.claudiocarige.desafio.application.port.in.FindCustomerByIdUseCase;
import br.com.claudiocarige.desafio.application.port.in.SearchCustomersUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final FindCustomerByIdUseCase findCustomerByIdUseCase;
    private final SearchCustomersUseCase searchCustomersUseCase;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            FindCustomerByIdUseCase findCustomerByIdUseCase,
            SearchCustomersUseCase searchCustomersUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.findCustomerByIdUseCase = findCustomerByIdUseCase;
        this.searchCustomersUseCase = searchCustomersUseCase;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {

        CustomerDto customer = createCustomerUseCase.execute(CustomerMapper.createCustomerRequestToCreateCustomerDto(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomerMapper.customerDtoToCustomerResponse(customer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findCustomerById(@PathVariable UUID id) {

        CustomerDto customer = findCustomerByIdUseCase.findCustomerById(id);

        return ResponseEntity.ok(CustomerMapper.customerDtoToCustomerResponse(customer));
    }

    @GetMapping
    public ResponseEntity<CustomerPageResponse> searchCustomers(
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "20", required = false) Integer size) {

        CustomerPageDto customersPage = searchCustomersUseCase.execute(page, size);
        List<CustomerResponse> customerResponses = customersPage.content().stream()
                .map(CustomerMapper::customerDtoToCustomerResponse)
                .toList();

        CustomerPageResponse response = CustomerMapper.customerPageToCustomerPageResponse(customerResponses, customersPage);

        return ResponseEntity.ok().body(response);
    }
}
