package br.com.claudiocarige.desafio.adapter.out.persistence;

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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UpdateCustomerRepository - Testes do adapter de persistência de atualização")
class UpdateCustomerRepositoryTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String EMAIL_NOVO = "joao.novo@email.com";
    private static final String NOME_NOVO = "João Silva Novo";

    private CustomerRepository customerRepository;
    private UpdateCustomerRepository updateCustomerRepository;

    private UUID idValido;
    private Customer customerParaAtualizar;
    private CustomerEntity entityExistente;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        updateCustomerRepository = new UpdateCustomerRepository(customerRepository);

        idValido = UUID.randomUUID();

        createCustomer();

        createEntityExistente();
    }

    @Nested
    @DisplayName("Caminho feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("Deve atualizar cliente e retornar Customer com os dados novos")
        void deveAtualizarClienteComSucesso() {
            CustomerEntity entityAtualizada = new CustomerEntity(
                    idValido, NOME_NOVO, CPF_VALIDO, EMAIL_NOVO, CustomerStatus.ACTIVE
            );

            when(customerRepository.findById(idValido)).thenReturn(Optional.of(entityExistente));
            when(customerRepository.save(any(CustomerEntity.class))).thenReturn(entityAtualizada);

            Customer resultado = updateCustomerRepository.update(customerParaAtualizar);

            assertNotNull(resultado);
            assertEquals(idValido, resultado.getId().value());
            assertEquals(NOME_NOVO, resultado.getName());
            assertEquals(EMAIL_NOVO, resultado.getEmail().value());
            assertEquals(CustomerStatus.ACTIVE, resultado.getStatus());

            verify(customerRepository, times(1)).findById(idValido);
            verify(customerRepository, times(1)).save(any(CustomerEntity.class));
        }
    }

    @Nested
    @DisplayName("Validação de Regras e Exceções")
    class ValidacoesERegras {

        @Test
        @DisplayName("Deve lançar NotFoundException quando cliente não for encontrado")
        void deveLancarNotFoundExceptionQuandoClienteNaoExiste() {
            when(customerRepository.findById(idValido)).thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> updateCustomerRepository.update(customerParaAtualizar));

            assertTrue(ex.getMessage().contains("Cliente"));
            verify(customerRepository, never()).save(any(CustomerEntity.class));
        }

        @Test
        @DisplayName("Deve lançar DomainException quando tentar alterar o CPF")
        void deveLancarDomainExceptionAoTentarAlterarCpf() {
            CustomerEntity entityComCpfDiferente = new CustomerEntity(
                    idValido, "João Silva", "00011122233", "joao@email.com", CustomerStatus.ACTIVE
            );
            
            when(customerRepository.findById(idValido)).thenReturn(Optional.of(entityComCpfDiferente));

            DomainException ex = assertThrows(DomainException.class,
                    () -> updateCustomerRepository.update(customerParaAtualizar));

            assertEquals("CPF não pode ser alterado.", ex.getMessage());
            verify(customerRepository, never()).save(any(CustomerEntity.class));
        }
    }

    private void createEntityExistente() {
        entityExistente = new CustomerEntity(
                idValido, "João Silva", CPF_VALIDO, "joao@email.com", CustomerStatus.ACTIVE
        );
    }

    private void createCustomer() {
        customerParaAtualizar = Customer.restore(
                new CustomerId(idValido),
                NOME_NOVO,
                new Cpf(CPF_VALIDO),
                new Email(EMAIL_NOVO),
                CustomerStatus.ACTIVE
        );
    }
}
