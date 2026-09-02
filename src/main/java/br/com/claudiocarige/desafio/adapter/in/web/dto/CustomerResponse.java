package br.com.claudiocarige.desafio.adapter.in.web.dto;

import br.com.claudiocarige.desafio.adapter.in.web.mapper.CustomerWebMapper;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CustomerResponse(
        @Schema(description = "Identificador único do cliente") UUID id,
        @Schema(description = "Nome completo do cliente", example = "Maria da Silva") String name,
        @Schema(description = "CPF do cliente, somente dígitos", example = "12345678901") String cpf,
        @Schema(description = "E-mail do cliente", example = "maria.silva@email.com") String email,
        @Schema(description = "Status atual do cliente") CustomerStatus status) {

    public static CustomerResponse from(CustomerDto customerDto) {
        return CustomerWebMapper.customerDtoToCustomerResponse(customerDto);
    }
}
