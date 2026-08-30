package br.com.claudiocarige.desafio.application.port.in;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.UpdateCustomerDto;

import java.util.UUID;

public interface UpdateCustomerUseCase {

    CustomerDto execute(UUID id, UpdateCustomerDto customerDto);
}
