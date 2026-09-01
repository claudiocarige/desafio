package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.port.out.SearchCustomersRepositoryPort;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.model.Customer;
import br.com.claudiocarige.desafio.domain.valueobject.Cpf;
import br.com.claudiocarige.desafio.domain.valueobject.CustomerId;
import br.com.claudiocarige.desafio.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FindCustomersByStatusUseCaseImpl - Testes de orquestração da busca por status")
class FindCustomersByStatusUseCaseImplTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String NOME_VALIDO = "João Silva";
    private static final String EMAIL_VALIDO = "joao@email.com";

    private SearchCustomersRepositoryPort searchCustomersRepository;
    private FindCustomersByStatusUseCaseImpl useCase;

    private Customer cliente;

    @BeforeEach
    void setUp() {
        searchCustomersRepository = mock(SearchCustomersRepositoryPort.class);
        useCase = new FindCustomersByStatusUseCaseImpl(searchCustomersRepository);

        createCustomer();
    }

    @Nested
    @DisplayName("Caminho feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("Deve buscar clientes por status com sucesso")
        void deveBuscarClientesPorStatusComSucesso() {
            SearchCustomersRepositoryPort.SearchResult result =
                    new SearchCustomersRepositoryPort.SearchResult(List.of(cliente), 1L);

            when(searchCustomersRepository.searchByStatus(CustomerStatus.ACTIVE, 0, 20)).thenReturn(result);

            CustomerPageDto resultado = useCase.execute(CustomerStatus.ACTIVE, 0, 20);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            assertEquals(NOME_VALIDO, resultado.content().get(0).name());
            assertEquals(1L, resultado.totalElements());
            verify(searchCustomersRepository, times(1)).searchByStatus(CustomerStatus.ACTIVE, 0, 20);
        }

        @Test
        @DisplayName("Deve aplicar valores padrão de página e tamanho quando nulos")
        void deveAplicarDefaultsQuandoPageESizeNulos() {
            SearchCustomersRepositoryPort.SearchResult result =
                    new SearchCustomersRepositoryPort.SearchResult(List.of(), 0L);

            when(searchCustomersRepository.searchByStatus(CustomerStatus.ACTIVE, 0, 20)).thenReturn(result);

            CustomerPageDto resultado = useCase.execute(CustomerStatus.ACTIVE, null, null);

            assertEquals(0, resultado.page());
            assertEquals(20, resultado.size());
            verify(searchCustomersRepository, times(1)).searchByStatus(CustomerStatus.ACTIVE, 0, 20);
        }
    }

    @Nested
    @DisplayName("Validação de entrada")
    class ValidacaoDeEntrada {

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando status for nulo")
        void deveLancarExcecaoQuandoStatusNulo() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(null, 0, 20));

            assertEquals("Status é obrigatório", ex.getMessage());
            verifyNoInteractions(searchCustomersRepository);
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando página for negativa")
        void deveLancarExcecaoQuandoPaginaNegativa() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(CustomerStatus.ACTIVE, -1, 20));

            assertEquals("Página deve ser maior ou igual a zero", ex.getMessage());
            verifyNoInteractions(searchCustomersRepository);
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando tamanho for menor ou igual a zero")
        void deveLancarExcecaoQuandoTamanhoInvalido() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(CustomerStatus.ACTIVE, 0, 0));

            assertEquals("Tamanho da página deve ser maior que zero", ex.getMessage());
            verifyNoInteractions(searchCustomersRepository);
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando tamanho exceder o máximo permitido")
        void deveLancarExcecaoQuandoTamanhoMaiorQueMaximo() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(CustomerStatus.ACTIVE, 0, 51));

            assertEquals("Tamanho da página não pode ser maior que 50", ex.getMessage());
            verifyNoInteractions(searchCustomersRepository);
        }
    }

    private void createCustomer() {
        cliente = Customer.restore(
                new CustomerId(UUID.randomUUID()),
                NOME_VALIDO,
                new Cpf(CPF_VALIDO),
                new Email(EMAIL_VALIDO),
                CustomerStatus.ACTIVE
        );
    }
}
