package br.com.claudiocarige.desafio.application.dto;

import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;

public record CustomerDto (
        Long id,
        String name,
        String cpf,
        String email,
        CustomerStatus status) {

}
