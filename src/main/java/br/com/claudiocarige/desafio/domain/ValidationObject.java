package br.com.claudiocarige.desafio.domain;


import br.com.claudiocarige.desafio.domain.exception.DomainException;

/**
 * Interface com métodos utilitários de validação de domínio.
 * Pode ser implementada por entidades e value objects que precisam de auto-validação.
 */
public interface ValidationObject {


    default <T> T assertArgumentNotNull(T val, String aMessage) {
        if (val == null) throw DomainException.with(aMessage + " não pode ser nulo");
        return val;
    }

    default String assertArgumentNotEmpty(String val, String aMessage) {

        if (val.isBlank()) throw DomainException.with(aMessage + " não pode ser vazio");
        return val;
    }


    default String assertArgumentMaxLength(String val, int length, String aMessage) {
        if (val != null && val.length() > length)
            throw DomainException.with(aMessage + " deve ter no máximo " + length + " caracteres");
        return val;
    }

    default String assertArgumentIsValidEmail(String email, String aMessage) {
        assertArgumentNotEmpty(email, aMessage);
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw DomainException.with(aMessage);
        }
        return email;
    }

    default String assertArgumentIsValidCpf(String cpf, String aMessage) {
        assertArgumentNotEmpty(cpf, aMessage);
        String cleanCpf = cpf.replaceAll("\\D", "");

        if (cleanCpf.length() != 11 || cleanCpf.matches("(\\d)\\1{10}")) {
            throw DomainException.with(aMessage);
        }

        try {
            int d1 = 0, d2 = 0;
            for (int i = 0; i < 9; i++) {
                int digit = Character.getNumericValue(cleanCpf.charAt(i));
                d1 += digit * (10 - i);
                d2 += digit * (11 - i);
            }

            d1 = 11 - (d1 % 11);
            if (d1 > 9) d1 = 0;

            d2 += d1 * 2;
            d2 = 11 - (d2 % 11);
            if (d2 > 9) d2 = 0;

            if (Character.getNumericValue(cleanCpf.charAt(9)) != d1 || Character.getNumericValue(cleanCpf.charAt(10)) != d2) {
                throw DomainException.with(aMessage);
            }
        } catch (Exception e) {
            throw DomainException.with(aMessage);
        }

        return cleanCpf;
    }

}

