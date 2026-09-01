package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.exception.NotFoundException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.CustomerId;
import br.com.claudiocarige.desafio.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FindCustomerByIdUseCaseImpl - Testes de orquestração da busca por ID")
class FindCustomerByIdUseCaseImplTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String EMAIL_VALIDO = "joao@email.com";
    private static final String NOME_VALIDO = "João Silva";

    private FindCustomerByIdRepositoryPort repository;
    private FindCustomerByIdUseCaseImpl useCase;

    private UUID idValido;
    private Customer customerEncontrado;

    @BeforeEach
    void setUp() {
        repository = mock(FindCustomerByIdRepositoryPort.class);
        useCase = new FindCustomerByIdUseCaseImpl(repository);

        idValido = UUID.randomUUID();

        createCustomer();
    }

    @Nested
    @DisplayName("Caminho feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("Deve retornar CustomerDto com dados corretos quando cliente é encontrado")
        void deveRetornarCustomerDtoComDadosCorretos() {
            when(repository.findById(idValido)).thenReturn(customerEncontrado);

            CustomerDto resultado = useCase.findCustomerById(idValido);

            assertNotNull(resultado);
            assertEquals(idValido, resultado.id());
            assertEquals(NOME_VALIDO, resultado.name());
            assertEquals(CPF_VALIDO, resultado.cpf());
            assertEquals(EMAIL_VALIDO, resultado.email());
            assertEquals(CustomerStatus.ACTIVE, resultado.status());
            verify(repository, times(1)).findById(idValido);
        }
    }

    @Nested
    @DisplayName("Validação de entrada")
    class ValidacaoDeEntrada {

        @Test
        @DisplayName("Deve lançar DomainException quando ID é nulo antes de chamar repositório")
        void deveLancarExcecaoQuandoIdNulo() {
            DomainException ex = assertThrows(DomainException.class,
                    () -> useCase.findCustomerById(null));

            assertTrue(ex.getMessage().contains("ID"));
            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("Propagação de exceções")
    class PropagacaoDeExcecoes {

        @Test
        @DisplayName("Deve propagar NotFoundException quando cliente não é encontrado na base")
        void devePropagaNotFoundExceptionQuandoClienteNaoEncontrado() {
            when(repository.findById(idValido))
                    .thenThrow(NotFoundException.of("Cliente", idValido));

            assertThrows(NotFoundException.class,
                    () -> useCase.findCustomerById(idValido));

            verify(repository, times(1)).findById(idValido);
        }

        @Test
        @DisplayName("Deve garantir que a mensagem de NotFoundException contém o ID informado")
        void deveMensagemDeNotFoundConterOId() {
            when(repository.findById(idValido))
                    .thenThrow(NotFoundException.of("Cliente", idValido));

            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> useCase.findCustomerById(idValido));

            assertTrue(ex.getMessage().contains(idValido.toString()));
        }
    }


    private void createCustomer() {
        customerEncontrado = Customer.restore(
                new CustomerId(idValido),
                NOME_VALIDO,
                new Cpf(CPF_VALIDO),
                new Email(EMAIL_VALIDO),
                CustomerStatus.ACTIVE
        );
    }
}
