package br.com.claudiocarige.desafio.application.port.in;

import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;

public interface SearchCustomersUseCase {

    CustomerPageDto execute(Integer page, Integer size);
}
