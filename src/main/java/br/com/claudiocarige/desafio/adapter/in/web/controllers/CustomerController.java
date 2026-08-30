package br.com.claudiocarige.desafio.adapter.in.web.controllers;

import br.com.claudiocarige.desafio.adapter.in.web.dto.CreateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerResponse;
import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.port.in.CreateCustomerUseCase;
import br.com.claudiocarige.desafio.application.port.in.FindCustomerByIdUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final FindCustomerByIdUseCase findCustomerByIdUseCase;

    public CustomerController(
            CreateCustomerUseCase createCustomerUseCase,
            FindCustomerByIdUseCase findCustomerByIdUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.findCustomerByIdUseCase = findCustomerByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {

        CustomerDto customer = createCustomerUseCase.execute(CustomerMapper.toCreateCustomerDto(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomerMapper.toResponse(customer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findCustomerById(@PathVariable UUID id) {

        CustomerDto customer = findCustomerByIdUseCase.findCustomerById(id);

        return ResponseEntity.ok(CustomerMapper.toResponse(customer));
    }

}
