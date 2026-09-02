package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerPageDto;
import br.com.claudiocarige.desafio.application.port.out.SearchCustomersRepositoryPort;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("SearchCustomersUseCaseImpl - Testes de orquestração da busca de clientes")
class SearchCustomersUseCaseImplTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String NOME_VALIDO = "João Silva";
    private static final String EMAIL_VALIDO = "joao@email.com";

    private SearchCustomersRepositoryPort searchCustomersRepository;
    private SearchCustomersUseCaseImpl useCase;

    private Customer cliente;

    @BeforeEach
    void setUp() {
        searchCustomersRepository = mock(SearchCustomersRepositoryPort.class);
        useCase = new SearchCustomersUseCaseImpl(searchCustomersRepository);

        createCustomer();
    }

    @Nested
    @DisplayName("Caminho feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("Deve buscar sem filtro de nome delegando para search()")
        void deveBuscarSemFiltroDeNome() {
            SearchCustomersRepositoryPort.SearchResult result =
                    new SearchCustomersRepositoryPort.SearchResult(List.of(cliente), 1L);

            when(searchCustomersRepository.search(0, 20)).thenReturn(result);

            CustomerPageDto resultado = useCase.execute(0, 20, null);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            assertEquals(NOME_VALIDO, resultado.content().get(0).name());
            verify(searchCustomersRepository, times(1)).search(0, 20);
            verify(searchCustomersRepository, never()).searchByName(any(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("Deve buscar com filtro de nome delegando para searchByName() com nome tratado (trim)")
        void deveBuscarComFiltroDeNome() {
            SearchCustomersRepositoryPort.SearchResult result =
                    new SearchCustomersRepositoryPort.SearchResult(List.of(cliente), 1L);

            when(searchCustomersRepository.searchByName(eq(NOME_VALIDO), eq(0), eq(20))).thenReturn(result);

            CustomerPageDto resultado = useCase.execute(0, 20, "  " + NOME_VALIDO + "  ");

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            verify(searchCustomersRepository, times(1)).searchByName(NOME_VALIDO, 0, 20);
            verify(searchCustomersRepository, never()).search(anyInt(), anyInt());
        }

        @Test
        @DisplayName("Deve tratar nome em branco como ausência de filtro")
        void deveTratarNomeEmBrancoComoSemFiltro() {
            SearchCustomersRepositoryPort.SearchResult result =
                    new SearchCustomersRepositoryPort.SearchResult(List.of(), 0L);

            when(searchCustomersRepository.search(0, 20)).thenReturn(result);

            useCase.execute(0, 20, "   ");

            verify(searchCustomersRepository, times(1)).search(0, 20);
            verify(searchCustomersRepository, never()).searchByName(any(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("Deve aplicar valores padrão de página e tamanho quando nulos")
        void deveAplicarDefaultsQuandoPageESizeNulos() {
            SearchCustomersRepositoryPort.SearchResult result =
                    new SearchCustomersRepositoryPort.SearchResult(List.of(), 0L);

            when(searchCustomersRepository.search(0, 20)).thenReturn(result);

            CustomerPageDto resultado = useCase.execute(null, null, null);

            assertEquals(0, resultado.page());
            assertEquals(20, resultado.size());
            verify(searchCustomersRepository, times(1)).search(0, 20);
        }
    }

    @Nested
    @DisplayName("Validação de entrada")
    class ValidacaoDeEntrada {

        @Test
        @DisplayName("Deve lançar DomainException quando página for negativa")
        void deveLancarExcecaoQuandoPaginaNegativa() {
              DomainException ex = assertThrows(DomainException.class,
                    () -> useCase.execute(-1, 20, null));

            assertEquals("Página deve ser maior ou igual a zero", ex.getMessage());
            verifyNoInteractions(searchCustomersRepository);
        }

        @Test
        @DisplayName("Deve lançar DomainException quando tamanho for menor ou igual a zero")
        void deveLancarExcecaoQuandoTamanhoInvalido() {
             DomainException ex = assertThrows(DomainException.class,
                    () -> useCase.execute(0, 0, null));

            assertEquals("Tamanho da página deve ser maior que zero", ex.getMessage());
            verifyNoInteractions(searchCustomersRepository);
        }

        @Test
        @DisplayName("Deve lançar DomainException quando tamanho exceder o máximo permitido")
        void deveLancarExcecaoQuandoTamanhoMaiorQueMaximo() {
             DomainException ex = assertThrows(DomainException.class,
                    () -> useCase.execute(0, 51, null));

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
