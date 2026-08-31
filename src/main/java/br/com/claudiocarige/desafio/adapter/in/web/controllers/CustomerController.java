package br.com.claudiocarige.desafio.adapter.in.web.controllers;

import br.com.claudiocarige.desafio.adapter.in.web.dto.CreateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerPageResponse;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerResponse;
import br.com.claudiocarige.desafio.adapter.in.web.dto.UpdateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.dto.CustomerScoreDto;
import br.com.claudiocarige.desafio.application.port.in.CreateCustomerUseCase;
import br.com.claudiocarige.desafio.application.port.in.FindCustomerByIdUseCase;
import br.com.claudiocarige.desafio.application.port.in.FindCustomersByStatusUseCase;
import br.com.claudiocarige.desafio.application.port.in.GetCustomerScoreUseCase;
import br.com.claudiocarige.desafio.application.port.in.SearchCustomersUseCase;
import br.com.claudiocarige.desafio.application.port.in.UpdateCustomerUseCase;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
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
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final GetCustomerScoreUseCase getCustomerScoreUseCase;
    private final FindCustomersByStatusUseCase findCustomersByStatusUseCase;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            FindCustomerByIdUseCase findCustomerByIdUseCase,
            SearchCustomersUseCase searchCustomersUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            GetCustomerScoreUseCase getCustomerScoreUseCase,
            FindCustomersByStatusUseCase findCustomersByStatusUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.findCustomerByIdUseCase = findCustomerByIdUseCase;
        this.searchCustomersUseCase = searchCustomersUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.getCustomerScoreUseCase = getCustomerScoreUseCase;
        this.findCustomersByStatusUseCase = findCustomersByStatusUseCase;
    }

    @PostMapping("/create")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {

        CustomerDto customer = createCustomerUseCase.execute(CustomerMapper.createCustomerRequestToCreateCustomerDto(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomerMapper.customerDtoToCustomerResponse(customer));
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<CustomerResponse> findCustomerById(@PathVariable UUID id) {

        CustomerDto customer = findCustomerByIdUseCase.findCustomerById(id);

        return ResponseEntity.ok(CustomerMapper.customerDtoToCustomerResponse(customer));
    }

    @GetMapping("/search")
    public ResponseEntity<CustomerPageResponse> searchCustomers(
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "20", required = false) Integer size,
            @RequestParam(required = false) String name) {

        CustomerPageDto customersPage = searchCustomersUseCase.execute(page, size, name);
        List<CustomerResponse> customerResponses = customersPage.content().stream()
                .map(CustomerMapper::customerDtoToCustomerResponse)
                .toList();

        CustomerPageResponse response = CustomerMapper.customerPageToCustomerPageResponse(customerResponses, customersPage);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        CustomerDto customer = updateCustomerUseCase.execute(
                id,
                CustomerMapper.updateCustomerRequestToUpdateCustomerDto(request)
        );

        return ResponseEntity.ok().body(CustomerMapper.customerDtoToCustomerResponse(customer));
    }

    @GetMapping("/{id}/score")
    public ResponseEntity<CustomerScoreDto> getCustomerScore(@PathVariable UUID id) {
        CustomerScoreDto score = getCustomerScoreUseCase.execute(id);
        return ResponseEntity.ok(score);
    }

    @GetMapping
    public ResponseEntity<CustomerPageResponse> findCustomersByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "20", required = false) Integer size) {

        CustomerStatus customerStatus = CustomerStatus.toEnum(status);
        CustomerPageDto customersPage = findCustomersByStatusUseCase.execute(customerStatus, page, size);
        List<CustomerResponse> customerResponses = customersPage.content().stream()
                .map(CustomerMapper::customerDtoToCustomerResponse)
                .toList();

        CustomerPageResponse response = CustomerMapper.customerPageToCustomerPageResponse(customerResponses, customersPage);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        // Este endpoint está a nivel informativo, pois não implementarei a deleção de Clientes.
        throw DomainException.with(
                "Clientes não podem ser deletados fisicamente. " +
                        "Por favor, utilize a rota de atualização (PUT) " +
                        "para alterar o status para BLOCKED ou INACTIVE."
        );
    }
}
