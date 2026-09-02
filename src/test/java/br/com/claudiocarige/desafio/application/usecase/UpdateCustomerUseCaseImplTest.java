package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.dto.UpdateCustomerDto;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.application.port.out.UpdateCustomerRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UpdateCustomerUseCaseImpl - Testes de orquestração do use case de atualização")
class UpdateCustomerUseCaseImplTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String EMAIL_ANTIGO = "joao@email.com";
    private static final String NOME_ANTIGO = "João Silva";
    
    private static final String NOVO_NOME = "João Silva Souza";
    private static final String NOVO_EMAIL = "joao.souza@email.com";

    private FindCustomerByIdRepositoryPort findCustomerByIdRepository;
    private UpdateCustomerRepositoryPort updateCustomerRepository;
    private UpdateCustomerUseCaseImpl useCase;

    private UUID idValido;
    private Customer clienteExistente;
    private UpdateCustomerDto dtoAtualizacao;

    @BeforeEach
    void setUp() {
        findCustomerByIdRepository = mock(FindCustomerByIdRepositoryPort.class);
        updateCustomerRepository = mock(UpdateCustomerRepositoryPort.class);
        useCase = new UpdateCustomerUseCaseImpl(findCustomerByIdRepository, updateCustomerRepository);

        idValido = UUID.randomUUID();

        createClienteExistente();

        createUpdateCustomerDto();
    }

    @Nested
    @DisplayName("Caminho feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("Deve atualizar cliente com sucesso e retornar CustomerDto atualizado")
        void deveAtualizarClienteComSucesso() {
            Customer clienteAtualizado = Customer.restore(
                    new CustomerId(idValido),
                    NOVO_NOME,
                    new Cpf(CPF_VALIDO),
                    new Email(NOVO_EMAIL),
                    CustomerStatus.ACTIVE
            );

            when(findCustomerByIdRepository.findById(idValido)).thenReturn(clienteExistente);
            when(updateCustomerRepository.update(any(Customer.class))).thenReturn(clienteAtualizado);

            CustomerDto resultado = useCase.execute(idValido, dtoAtualizacao);

            assertNotNull(resultado);
            assertEquals(idValido, resultado.id());
            assertEquals(NOVO_NOME, resultado.name());
            assertEquals(CPF_VALIDO, resultado.cpf());
            assertEquals(NOVO_EMAIL, resultado.email());
            assertEquals(CustomerStatus.ACTIVE, resultado.status());

            verify(findCustomerByIdRepository, times(1)).findById(idValido);
            verify(updateCustomerRepository, times(1)).update(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("Validação de entrada")
    class ValidacaoDeEntrada {

        @Test
        @DisplayName("Deve lançar DomainException quando ID é nulo")
        void deveLancarExcecaoQuandoIdNulo() {
            DomainException ex = assertThrows(DomainException.class,
                    () -> useCase.execute(null, dtoAtualizacao));

            assertEquals("ID do cliente é obrigatório", ex.getMessage());
            verifyNoInteractions(findCustomerByIdRepository, updateCustomerRepository);
        }
    }

    @Nested
    @DisplayName("Propagação de exceções e Regras de Negócio")
    class PropagacaoDeExcecoesERegras {

        @Test
        @DisplayName("Deve propagar NotFoundException se cliente não existir")
        void devePropagarNotFoundException() {
            when(findCustomerByIdRepository.findById(idValido))
                    .thenThrow(NotFoundException.of("Cliente", idValido));

            assertThrows(NotFoundException.class,
                    () -> useCase.execute(idValido, dtoAtualizacao));

            verify(findCustomerByIdRepository, times(1)).findById(idValido);
            verifyNoInteractions(updateCustomerRepository);
        }

        @Test
        @DisplayName("Deve manter o status original se o status no DTO for nulo (atualização parcial)")
        void deveManterStatusAntigoSeNuloNoDto() {
            UpdateCustomerDto dtoParcial = new UpdateCustomerDto(
                    NOVO_NOME,
                    new Cpf(CPF_VALIDO),
                    new Email(NOVO_EMAIL),
                    null
            );

            Customer clienteAtualizado = Customer.restore(
                    new CustomerId(idValido),
                    NOVO_NOME,
                    new Cpf(CPF_VALIDO),
                    new Email(NOVO_EMAIL),
                    CustomerStatus.ACTIVE
            );

            when(findCustomerByIdRepository.findById(idValido)).thenReturn(clienteExistente);
            when(updateCustomerRepository.update(any(Customer.class))).thenReturn(clienteAtualizado);

            CustomerDto resultado = useCase.execute(idValido, dtoParcial);

            assertEquals(CustomerStatus.ACTIVE, resultado.status());
            verify(updateCustomerRepository, times(1)).update(any(Customer.class));
        }
    }

    private void createUpdateCustomerDto() {
        dtoAtualizacao = new UpdateCustomerDto(
                NOVO_NOME,
                new Cpf(CPF_VALIDO),
                new Email(NOVO_EMAIL),
                CustomerStatus.ACTIVE
        );
    }

    private void createClienteExistente() {
        clienteExistente = Customer.restore(
                new CustomerId(idValido),
                NOME_ANTIGO,
                new Cpf(CPF_VALIDO),
                new Email(EMAIL_ANTIGO),
                CustomerStatus.ACTIVE
        );
    }

}
