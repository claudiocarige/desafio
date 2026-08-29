package br.com.claudiocarige.desafio.application.port.in;

import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;

public interface CreateCustomerUseCase {

    CustomerDto execute(CreateCustomerDto customerDto);
}
