package br.com.claudiocarige.desafio.application.port.in;

import br.com.claudiocarige.desafio.application.dto.CustomerScoreDto;

import java.util.UUID;

public interface GetCustomerScoreUseCase {

    CustomerScoreDto execute(UUID customerId);
}
