package br.com.claudiocarige.desafio.domain.model;


import br.com.claudiocarige.desafio.domain.ValidationObject;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.CustomerId;
import br.com.claudiocarige.desafio.domain.valueobject.Email;


public final class Customer implements ValidationObject {

    private final CustomerId id;
    private String name;
    private final Cpf cpf;
    private Email email;
    private CustomerStatus status;

    private Customer(
            CustomerId id,
            String name,
            Cpf cpf,
            Email email,
            CustomerStatus status) {

        assertArgumentNotNull(name, "Nome");
        assertArgumentNotEmpty(name, "Nome");
        assertArgumentMaxLength(name, 255, "Nome deve ter no máximo 255 caracteres");
        assertArgumentNotNull(cpf, "CPF");
        assertArgumentNotNull(email, "E-mail");

        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.status = status;
    }

    public static Customer create(
            String name,
            Cpf cpf,
            Email email) {

        return new Customer(
                null,
                name,
                cpf,
                email,
                CustomerStatus.ACTIVE
        );
    }

    public static Customer restore(
            CustomerId id,
            String name,
            Cpf cpf,
            Email email,
            CustomerStatus status) {

        validateId(id);

        return new Customer(
                id,
                name,
                cpf,
                email,
                status
        );
    }

    public void activateStatus() {
        if (status == CustomerStatus.ACTIVE) {
            return;
        }

        status = CustomerStatus.ACTIVE;
    }

    public void blockStatus() {
        if (status == CustomerStatus.BLOCKED) {
            return;
        }

        status = CustomerStatus.BLOCKED;
    }

    public void inactivateStatus() {
        if (status == CustomerStatus.INACTIVE) {
            return;
        }

        status = CustomerStatus.INACTIVE;
    }

    public String getName() {
        return name;
    }

    public CustomerId getId() {
        return id;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void changeNewEmail(Email newEmail) {
        this.email = assertArgumentNotNull(newEmail, "E-mail");
    }


    private static void validateId(CustomerId id) {
        if (id == null) {
            throw DomainException.with("ID do cliente não pode ser nulo");
        }
    }

    public void changeNewName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    private void validateName(String newName) {
        var fieldName = "Nome";
        assertArgumentNotNull(newName, fieldName);
        assertArgumentNotEmpty(newName, fieldName);
        assertArgumentMaxLength(newName, 255, fieldName);
    }

}
