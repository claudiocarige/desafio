package br.com.claudiocarige.desafio.application.port.in;

import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;

public interface FindCustomersByStatusUseCase {

    CustomerPageDto execute(CustomerStatus status, Integer page, Integer size);
}
