package br.com.claudiocarige.desafio.application.port.out;

import br.com.claudiocarige.desafio.application.dto.CustomerScoreDto;

public interface CustomerScoreClientPort {

    CustomerScoreDto getScoreByCpf(String cpf);
}
