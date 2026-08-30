package br.com.claudiocarige.desafio.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record UpdateCustomerRequest(

        @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
        String name,

        @CPF(message = "CPF inválido")
        @Pattern(regexp = "^(\\d{11})?$", message = "CPF deve possuir 11 dígitos")
        String cpf,

        @Email(message = "E-mail inválido")
        @Size(max = 255, message = "E-mail deve ter no máximo 255 caracteres")
        String email,

        String status) {
}
