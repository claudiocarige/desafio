package br.com.claudiocarige.desafio.application.port.in;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;

import java.util.UUID;

public interface FindCustomerByIdUseCase {

    CustomerDto execute(UUID id);
}
