package br.com.claudiocarige.desafio.application.dto;

import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;

import java.util.UUID;

public record CustomerDto (
        UUID id,
        String name,
        String cpf,
        String email,
        CustomerStatus status) {

}
