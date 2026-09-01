package br.com.claudiocarige.desafio.application.dto;

import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.Email;

public record UpdateCustomerDto(
        String name,
        Cpf cpf,
        Email email,
        CustomerStatus status) {
}
