package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.NotFoundException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("FindCustomerByIdRepository - Testes do adapter JdbcTemplate de busca por ID")
class FindCustomerByIdRepositoryTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String EMAIL_VALIDO = "joao@email.com";
    private static final String NOME_VALIDO = "João Silva";

    private JdbcTemplate jdbcTemplate;
    private FindCustomerByIdRepository repository;

    private UUID idValido;
    private CustomerEntity entityEncontrada;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        repository = new FindCustomerByIdRepository(jdbcTemplate);

        idValido = UUID.randomUUID();

        createEntity();
    }

    @SuppressWarnings("unchecked")
    private void mockQueryRetornando(List<CustomerEntity> resultado) {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(resultado);
    }

    @Nested
    @DisplayName("Caminho feliz - cliente encontrado")
    class ClienteEncontrado {

        @Test
        @DisplayName("Deve retornar Customer com dados corretos quando JdbcTemplate retorna resultado")
        void deveRetornarCustomerComDadosCorretos() {
            mockQueryRetornando(List.of(entityEncontrada));

            Customer resultado = repository.findById(idValido);

            assertNotNull(resultado);
            assertEquals(idValido, resultado.getId().value());
            assertEquals(NOME_VALIDO, resultado.getName());
            assertEquals(CPF_VALIDO, resultado.getCpf().value());
            assertEquals(EMAIL_VALIDO, resultado.getEmail().value());
            assertEquals(CustomerStatus.ACTIVE, resultado.getStatus());
        }

        @Test
        @DisplayName("Deve chamar JdbcTemplate exatamente uma vez com o ID convertido para String")
        void deveChamarJdbcTemplateUmaVez() {
            mockQueryRetornando(List.of(entityEncontrada));

            repository.findById(idValido);

            verify(jdbcTemplate, times(1))
                    .query(anyString(), any(RowMapper.class), eq(idValido.toString()));
        }

        @Test
        @DisplayName("Deve preservar o status do cliente retornado pela query")
        void devePreservarStatusDoCliente() {
            CustomerEntity entityBloqueada = new CustomerEntity(
                    idValido, NOME_VALIDO, CPF_VALIDO, EMAIL_VALIDO, CustomerStatus.BLOCKED
            );
            mockQueryRetornando(List.of(entityBloqueada));

            Customer resultado = repository.findById(idValido);

            assertEquals(CustomerStatus.BLOCKED, resultado.getStatus());
        }
    }

    @Nested
    @DisplayName("Cliente não encontrado")
    class ClienteNaoEncontrado {

        @Test
        @DisplayName("Deve lançar NotFoundException quando JdbcTemplate retorna lista vazia")
        void deveLancarNotFoundExceptionQuandoListaVazia() {
            mockQueryRetornando(List.of());

            assertThrows(NotFoundException.class, () -> repository.findById(idValido));
        }

        @Test
        @DisplayName("Deve incluir o ID na mensagem da NotFoundException")
        void deveMensagemDeExcecaoConterOId() {
            mockQueryRetornando(List.of());

            NotFoundException ex = assertThrows(NotFoundException.class,
                    () -> repository.findById(idValido));

            assertTrue(ex.getMessage().contains(idValido.toString()));
        }
    }

    private void createEntity() {
        entityEncontrada = new CustomerEntity(
                idValido, NOME_VALIDO, CPF_VALIDO, EMAIL_VALIDO, CustomerStatus.ACTIVE
        );
    }
}
