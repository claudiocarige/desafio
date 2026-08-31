package br.com.claudiocarige.desafio.domain.model;

import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.CustomerId;
import br.com.claudiocarige.desafio.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer - Testes de domínio")
class CustomerTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String EMAIL_VALIDO = "joao@email.com";
    private static final String NOME_VALIDO = "João Silva";

    private Customer customer;

    @BeforeEach
    void setUp() {
        createCustomer();
    }

    @Nested
    @DisplayName("Customer.create()")
    class CriacaoDeCliente {

        @Test
        @DisplayName("Deve criar cliente com status ACTIVE por padrão")
        void deveCriarClienteComStatusActiveporPadrao() {
            assertEquals(NOME_VALIDO, customer.getName());
            assertEquals(CPF_VALIDO, customer.getCpf().value());
            assertEquals(EMAIL_VALIDO, customer.getEmail().value());
            assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
            assertNull(customer.getId());
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome é nulo")
        void deveLancarExcecaoQuandoNomeNulo() {
            DomainException ex = assertThrows(DomainException.class,
                    () -> Customer.create(null, new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO)));

            assertTrue(ex.getMessage().contains("Nome"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome é em branco")
        void deveLancarExcecaoQuandoNomeEmBranco() {
            DomainException ex = assertThrows(DomainException.class,
                    () -> Customer.create("   ", new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO)));

            assertTrue(ex.getMessage().contains("Nome"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome ultrapassa 255 caracteres")
        void deveLancarExcecaoQuandoNomeUltrapassaLimiteDeCaracteres() {
            String nomeGrande = "A".repeat(256);

            DomainException ex = assertThrows(DomainException.class,
                    () -> Customer.create(nomeGrande, new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO)));

            assertTrue(ex.getMessage().contains("255"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando CPF é nulo")
        void deveLancarExcecaoQuandoCpfNulo() {
            DomainException ex = assertThrows(DomainException.class,
                    () -> Customer.create(NOME_VALIDO, null, new Email(EMAIL_VALIDO)));

            assertTrue(ex.getMessage().contains("CPF"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando e-mail é nulo")
        void deveLancarExcecaoQuandoEmailNulo() {
            DomainException ex = assertThrows(DomainException.class,
                    () -> Customer.create(NOME_VALIDO, new Cpf(CPF_VALIDO), null));

            assertTrue(ex.getMessage().contains("E-mail"));
        }
    }

    @Nested
    @DisplayName("Customer.restore()")
    class RestauracaoDeCliente {

        @Test
        @DisplayName("Deve lançar exceção quando ID é nulo no restore")
        void deveLancarExcecaoQuandoIdNuloNoRestore() {
            DomainException ex = assertThrows(DomainException.class,
                    () -> Customer.restore(null, NOME_VALIDO, new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO), CustomerStatus.ACTIVE));

            assertTrue(ex.getMessage().contains("ID"));
        }

        @Test
        @DisplayName("Deve restaurar cliente com todos os dados corretamente")
        void deveRestaurarClienteComTodosOsDados() {
            UUID id = UUID.randomUUID();
            Customer restaurado = Customer.restore(
                    new CustomerId(id), NOME_VALIDO,
                    new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO), CustomerStatus.BLOCKED);

            assertEquals(id, restaurado.getId().value());
            assertEquals(CustomerStatus.BLOCKED, restaurado.getStatus());
        }
    }

    @Nested
    @DisplayName("Transições de status")
    class TransicoesDeStatus {

        @Test
        @DisplayName("Deve manter status ACTIVE quando já é ACTIVE (idempotência)")
        void deveManterStatusActiveQuandoJaEhActive() {
            customer.activateStatus();
            assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
        }

        @Test
        @DisplayName("Deve mudar status para BLOCKED quando cliente está ACTIVE")
        void deveMudarStatusParaBlockedQuandoAtivo() {
            customer.blockStatus();
            assertEquals(CustomerStatus.BLOCKED, customer.getStatus());
        }

        @Test
        @DisplayName("Deve mudar status para INACTIVE quando cliente está ACTIVE")
        void deveMudarStatusParaInactiveQuandoAtivo() {
            customer.inactivateStatus();
            assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
        }

        @Test
        @DisplayName("Deve manter status BLOCKED quando já é BLOCKED (idempotência)")
        void deveManterStatusBlockedQuandoJaEhBlocked() {
            Customer clienteBloqueado = Customer.restore(
                    new CustomerId(UUID.randomUUID()), NOME_VALIDO,
                    new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO), CustomerStatus.BLOCKED);

            clienteBloqueado.blockStatus();

            assertEquals(CustomerStatus.BLOCKED, clienteBloqueado.getStatus());
        }
    }

    @Nested
    @DisplayName("Alteração de dados do cliente")
    class AlteracaoDeDados {

        @Test
        @DisplayName("Deve lançar exceção ao alterar nome para em branco")
        void deveLancarExcecaoAoAlterarNomeParaEmBranco() {
            DomainException ex = assertThrows(DomainException.class, () -> customer.changeNewName("  "));
            assertTrue(ex.getMessage().contains("Nome"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao alterar e-mail para nulo")
        void deveLancarExcecaoAoAlterarEmailParaNulo() {
            DomainException ex = assertThrows(DomainException.class, () -> customer.changeNewEmail(null));
            assertTrue(ex.getMessage().contains("E-mail"));
        }

        @Test
        @DisplayName("Deve alterar nome com sucesso")
        void deveAlterarNomeComSucesso() {
            customer.changeNewName("Maria Costa");
            assertEquals("Maria Costa", customer.getName());
        }
    }

    @Nested
    @DisplayName("Value Object: Cpf")
    class ValidacaoDeCpf {

        @Test
        @DisplayName("Deve lançar exceção para CPF com menos de 11 dígitos")
        void deveLancarExcecaoParaCpfComMenosDeOnzeDigitos() {
            DomainException ex = assertThrows(DomainException.class, () -> new Cpf("1234567"));
            assertTrue(ex.getMessage().contains("CPF"));
        }

        @Test
        @DisplayName("Deve lançar exceção para CPF com todos os dígitos iguais")
        void deveLancarExcecaoParaCpfComDigitosIguais() {
            DomainException ex = assertThrows(DomainException.class, () -> new Cpf("11111111111"));
            assertTrue(ex.getMessage().contains("CPF"));
        }

        @Test
        @DisplayName("Deve lançar exceção para CPF com dígitos verificadores errados")
        void deveLancarExcecaoParaCpfComDigitosVerificadoresErrados() {
            DomainException ex = assertThrows(DomainException.class, () -> new Cpf("12345678900"));
            assertTrue(ex.getMessage().contains("CPF"));
        }

        @Test
        @DisplayName("Deve lançar exceção para CPF nulo")
        void deveLancarExcecaoParaCpfNulo() {
            DomainException ex = assertThrows(DomainException.class, () -> new Cpf(null));
            assertTrue(ex.getMessage().contains("CPF"));
        }

        @Test
        @DisplayName("Deve aceitar CPF válido com formatação (pontos e traço) e normalizar para só dígitos")
        void deveAceitarCpfValidoComFormatacao() {
            Cpf cpf = new Cpf("855.410.250-95");
            assertEquals(CPF_VALIDO, cpf.value());
        }
    }

    @Nested
    @DisplayName("Value Object: Email")
    class ValidacaoDeEmail {

        @Test
        @DisplayName("Deve lançar exceção para e-mail sem @")
        void deveLancarExcecaoParaEmailSemArroba() {
            DomainException ex = assertThrows(DomainException.class, () -> new Email("emailsemarroba.com"));
            assertTrue(ex.getMessage().contains("E-mail"));
        }

        @Test
        @DisplayName("Deve lançar exceção para e-mail nulo")
        void deveLancarExcecaoParaEmailNulo() {
            DomainException ex = assertThrows(DomainException.class, () -> new Email(null));
            assertTrue(ex.getMessage().contains("E-mail"));
        }

        @Test
        @DisplayName("Deve lançar exceção para e-mail em branco")
        void deveLancarExcecaoParaEmailEmBranco() {
            DomainException ex = assertThrows(DomainException.class, () -> new Email("  "));
            assertTrue(ex.getMessage().contains("E-mail"));
        }

        @Test
        @DisplayName("Deve normalizar e-mail para letras minúsculas")
        void deveNormalizarEmailParaMinusculo() {
            Email email = new Email("JOAO@EMAIL.COM");
            assertEquals("joao@email.com", email.value());
        }
    }

    private void createCustomer() {
        customer = Customer.create(NOME_VALIDO, new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO));
    }

}
