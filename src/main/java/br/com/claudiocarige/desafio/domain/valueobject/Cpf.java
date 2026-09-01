package br.com.claudiocarige.desafio.domain.valueobject;

import br.com.claudiocarige.desafio.domain.ValidationObject;

public record Cpf (String value) implements ValidationObject {

    public Cpf {
        assertArgumentNotNull(value, "CPF");
        assertArgumentNotEmpty(value, "CPF");
        value = value.replaceAll("\\D", "");
        assertArgumentIsValidCpf(value, "CPF inválido");
    }
}
