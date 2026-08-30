package br.com.claudiocarige.desafio.domain.enums;

import br.com.claudiocarige.desafio.domain.exception.DomainException;

public enum CustomerStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    public static CustomerStatus toEnum(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return CustomerStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw DomainException.with(
                    "Status '%s' inválido. Valores aceitos: ACTIVE, INACTIVE, BLOCKED.".formatted(status)
            );
        }
    }
}
