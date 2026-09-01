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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
public class CustomerController implements CustomerControllerApi {

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

    @Override
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {

        CustomerDto customer = createCustomerUseCase.execute(CustomerMapper.createCustomerRequestToCreateCustomerDto(request));
        URI location = MvcUriComponentsBuilder.fromMethodCall(
                MvcUriComponentsBuilder.on(CustomerController.class)
                        .findCustomerById(customer.id()))
                .build()
                .toUri();
        return ResponseEntity.created(location)
                .body(CustomerMapper.customerDtoToCustomerResponse(customer));
    }

    @Override
    public ResponseEntity<CustomerResponse> findCustomerById(@PathVariable UUID id) {

        CustomerDto customer = findCustomerByIdUseCase.findCustomerById(id);

        return ResponseEntity.ok(CustomerMapper.customerDtoToCustomerResponse(customer));
    }

    @Override
    public ResponseEntity<CustomerPageResponse> searchCustomers (Integer page, Integer size, String name) {

        CustomerPageDto customersPage = searchCustomersUseCase.execute(page, size, name);
        List<CustomerResponse> customerResponses = customersPage.content().stream()
                .map(CustomerMapper::customerDtoToCustomerResponse)
                .toList();

        CustomerPageResponse response = CustomerMapper.customerPageToCustomerPageResponse(customerResponses, customersPage);

        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        CustomerDto customer = updateCustomerUseCase.execute(
                id,
                CustomerMapper.updateCustomerRequestToUpdateCustomerDto(request)
        );

        return ResponseEntity.ok().body(CustomerMapper.customerDtoToCustomerResponse(customer));
    }

    @Override
    public ResponseEntity<CustomerScoreDto> getCustomerScore(@PathVariable UUID id) {
        CustomerScoreDto score = getCustomerScoreUseCase.execute(id);
        return ResponseEntity.ok(score);
    }

    @Override
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

    @Override
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        // Este endpoint está a nivel informativo, pois não implementarei a deleção de Clientes.
        throw DomainException.with(
                "Clientes não podem ser deletados fisicamente. " +
                        "Por favor, utilize a rota de atualização (PUT) " +
                        "para alterar o status para BLOCKED ou INACTIVE."
        );
    }
}
