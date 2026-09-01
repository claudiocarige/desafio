package br.com.claudiocarige.desafio.application.dto;

import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.Email;

public record CreateCustomerDto(
        String name,
        Cpf cpf,
        Email email) {
}
