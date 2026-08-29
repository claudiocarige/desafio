package br.com.claudiocarige.desafio.application.port.in;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;

public interface FindCustomerByIdUseCase {

    CustomerDto execute(Long id);
}
