package br.com.claudiocarige.desafio.domain.valueobject;

import br.com.claudiocarige.desafio.domain.ValidationObject;

public record Email(String value) implements ValidationObject {

    public Email {
        assertArgumentNotNull(value, "E-mail");
        assertArgumentNotEmpty(value, "E-mail");
        value = value.trim().toLowerCase(java.util.Locale.ROOT);
        assertArgumentMaxLength(value, 255, "E-mail deve ter no máximo 255 caracteres");
        assertArgumentIsValidEmail(value, "E-mail inválido");
    }
}
