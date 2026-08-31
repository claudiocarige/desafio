package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
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

@DisplayName("CreateCustomerRepository - Testes do adapter de persistência de criação")
class CreateCustomerRepositoryTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String EMAIL_VALIDO = "joao@email.com";
    private static final String NOME_VALIDO = "João Silva";

    private CustomerRepository customerRepository;
    private CreateCustomerRepository createCustomerRepository;

    private Customer customer;
    private CustomerEntity entityExistente;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        createCustomerRepository = new CreateCustomerRepository(customerRepository);

        createCustomer();

        createEntityExistente();
    }

    private CustomerEntity entitySalva(UUID id) {
        return new CustomerEntity(id, NOME_VALIDO, CPF_VALIDO, EMAIL_VALIDO, CustomerStatus.ACTIVE);
    }

    @Nested
    @DisplayName("Caminho feliz - salvamento com sucesso")
    class SalvamentoComSucesso {

        @Test
        @DisplayName("Deve salvar cliente e retornar Customer com dados corretos")
        void deveSalvarClienteComSucesso() {
            UUID idGerado = UUID.randomUUID();

            when(customerRepository.findByCpf(CPF_VALIDO)).thenReturn(Optional.empty());
            when(customerRepository.save(any(CustomerEntity.class))).thenReturn(entitySalva(idGerado));

            Customer resultado = createCustomerRepository.save(customer);

            assertNotNull(resultado.getId());
            assertEquals(NOME_VALIDO, resultado.getName());
            assertEquals(CPF_VALIDO, resultado.getCpf().value());
            assertEquals(EMAIL_VALIDO, resultado.getEmail().value());
            assertEquals(CustomerStatus.ACTIVE, resultado.getStatus());

            verify(customerRepository, times(1)).findByCpf(CPF_VALIDO);
            verify(customerRepository, times(1)).save(any(CustomerEntity.class));
        }

        @Test
        @DisplayName("Deve mapear entidade persistida de volta para domínio preservando o ID gerado pelo banco")
        void deveMapearEntidadeDeRetornoParaDominioCorretamente() {
            UUID idGerado = UUID.randomUUID();

            when(customerRepository.findByCpf(CPF_VALIDO)).thenReturn(Optional.empty());
            when(customerRepository.save(any(CustomerEntity.class))).thenReturn(entitySalva(idGerado));

            Customer resultado = createCustomerRepository.save(customer);

            assertNotNull(resultado.getId());
            assertEquals(idGerado, resultado.getId().value());
        }
    }

    @Nested
    @DisplayName("Regra de negócio - unicidade de CPF")
    class UnicidadeDeCpf {

        @Test
        @DisplayName("Deve lançar DomainException quando CPF já existe na base")
        void deveLancarDomainExceptionQuandoCpfJaExiste() {
            when(customerRepository.findByCpf(CPF_VALIDO)).thenReturn(Optional.of(entityExistente));

            DomainException ex = assertThrows(DomainException.class,
                    () -> createCustomerRepository.save(customer));

            assertTrue(ex.getMessage().contains("CPF"));
            verify(customerRepository, never()).save(any(CustomerEntity.class));
        }

        @Test
        @DisplayName("Não deve expor o CPF completo na mensagem de erro (segurança)")
        void deveMascararCpfNaMensagemDeErroDeduplicidade() {
            when(customerRepository.findByCpf(CPF_VALIDO)).thenReturn(Optional.of(entityExistente));

            DomainException ex = assertThrows(DomainException.class,
                    () -> createCustomerRepository.save(customer));

            assertFalse(ex.getMessage().contains(CPF_VALIDO),
                    "A mensagem de erro não deve expor o CPF completo");
        }

        @Test
        @DisplayName("Deve verificar CPF antes de qualquer operação de persistência")
        void deveVerificarCpfAntesDeQualquerPersistencia() {
            when(customerRepository.findByCpf(CPF_VALIDO)).thenReturn(Optional.of(entityExistente));

            assertThrows(DomainException.class, () -> createCustomerRepository.save(customer));

            verify(customerRepository, times(1)).findByCpf(CPF_VALIDO);
            verify(customerRepository, never()).save(any());
        }
    }

    private void createEntityExistente() {
        entityExistente = new CustomerEntity(
                UUID.randomUUID(), "Outro Cliente", CPF_VALIDO, "outro@email.com", CustomerStatus.ACTIVE
        );
    }

    private void createCustomer() {
        customer = Customer.create(NOME_VALIDO, new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO));
    }
}
