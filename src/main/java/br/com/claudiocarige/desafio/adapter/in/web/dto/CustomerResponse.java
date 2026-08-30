package br.com.claudiocarige.desafio.adapter.in.web.dto;

import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String cpf,
        String email,
        CustomerStatus status) {

    public static CustomerResponse from(CustomerDto customerDto) {
        return CustomerMapper.customerDtoToCustomerResponse(customerDto);
    }
}
