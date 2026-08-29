package br.com.claudiocarige.desafio.adapter.in.web.dto;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.model.Customer;
import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerMapper;

public record CustomerResponse(
        Long id,
        String name,
        String cpf,
        String email,
        CustomerStatus status) {

    public static CustomerResponse from(CustomerDto customerDto) {
        return CustomerMapper.toResponse(customerDto);
    }
}
