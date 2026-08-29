package br.com.claudiocarige.desafio.adapter.in.web.dto;

import jakarta.validation.constraints.*;

public record CreateCustomerRequest(

        @NotBlank(message = "Nome é obrigatório")
        @NotNull(message = "Nome não pode ser nulo")
        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String name,

        @NotBlank(message = "CPF é obrigatório")
        @NotNull(message = "CPF não pode ser nulo")
        @Pattern(regexp = "\\d{11}", message = "CPF deve possuir 11 dígitos")
        String cpf,

        @NotBlank(message = "E-mail é obrigatório")
        @NotNull(message = "E-mail não pode ser nulo")
        @Email(message = "E-mail inválido")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
        String email) {
}
