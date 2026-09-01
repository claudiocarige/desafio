package br.com.claudiocarige.desafio.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record UpdateCustomerRequest(

        @Schema(description = "Nome completo do cliente", example = "Maria da Silva")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String name,

        @Schema(description = "CPF do cliente, somente dígitos", example = "12345678901")
        @CPF(message = "CPF inválido")
        @Pattern(regexp = "^(\\d{11})?$", message = "CPF deve possuir 11 dígitos")
        String cpf,

        @Schema(description = "E-mail do cliente", example = "maria.silva@email.com")
        @Email(message = "E-mail inválido")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
        String email,

        @Schema(description = "Novo status do cliente", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "BLOCKED"})
        String status) {
}
