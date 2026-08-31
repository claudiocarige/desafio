package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerScoreDto;
import br.com.claudiocarige.desafio.application.port.out.CustomerScoreClientPort;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.exception.ExternalScoreServiceException;
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

@DisplayName("GetCustomerScoreUseCaseImpl - Testes de orquestração da busca de score")
class GetCustomerScoreUseCaseImplTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String NOME_VALIDO = "João Silva";
    private static final String EMAIL_VALIDO = "joao@email.com";
    private static final String LOW_RISK = "LOW_RISK";

    private FindCustomerByIdRepositoryPort findCustomerByIdRepository;
    private CustomerScoreClientPort customerScoreClient;
    private GetCustomerScoreUseCaseImpl useCase;

    private UUID idValido;
    private Customer customer;

    @BeforeEach
    void setUp() {
        findCustomerByIdRepository = mock(FindCustomerByIdRepositoryPort.class);
        customerScoreClient = mock(CustomerScoreClientPort.class);
        useCase = new GetCustomerScoreUseCaseImpl(findCustomerByIdRepository, customerScoreClient);

        idValido = UUID.randomUUID();

        createCustomer();
    }

    @Nested
    @DisplayName("Caminho feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("Deve retornar o score do cliente com sucesso")
        void deveRetornarCustomerScoreComSucesso() {
            CustomerScoreDto scoreMock = new CustomerScoreDto(CPF_VALIDO, 855, LOW_RISK);

            when(findCustomerByIdRepository.findById(idValido)).thenReturn(customer);
            when(customerScoreClient.getScoreByCpf(CPF_VALIDO)).thenReturn(scoreMock);

            CustomerScoreDto resultado = useCase.execute(idValido);

            assertNotNull(resultado);
            assertEquals(CPF_VALIDO, resultado.cpf());
            assertEquals(855, resultado.score());
            assertEquals(LOW_RISK, resultado.classification());

            verify(findCustomerByIdRepository, times(1)).findById(idValido);
            verify(customerScoreClient, times(1)).getScoreByCpf(CPF_VALIDO);
        }
    }

    @Nested
    @DisplayName("Validação de entrada")
    class ValidacaoDeEntrada {

        @Test
        @DisplayName("Deve lançar DomainException quando ID for nulo")
        void deveLancarDomainExceptionQuandoIdNulo() {
            DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(null));

            assertEquals("ID do cliente é obrigatório", ex.getMessage());
            verifyNoInteractions(findCustomerByIdRepository, customerScoreClient);
        }
    }

    @Nested
    @DisplayName("Tratamento e Propagação de Exceções")
    class PropagacaoExcecoes {

        @Test
        @DisplayName("Deve propagar NotFoundException quando cliente não for encontrado")
        void devePropagarNotFoundException() {
            when(findCustomerByIdRepository.findById(idValido)).thenThrow(NotFoundException.of("Cliente", idValido));

            assertThrows(NotFoundException.class, () -> useCase.execute(idValido));

            verify(findCustomerByIdRepository, times(1)).findById(idValido);
            verifyNoInteractions(customerScoreClient);
        }

        @Test
        @DisplayName("Deve propagar ExternalScoreServiceException quando cliente externo falhar")
        void devePropagarExternalScoreServiceException() {
            when(findCustomerByIdRepository.findById(idValido)).thenReturn(customer);
            when(customerScoreClient.getScoreByCpf(CPF_VALIDO)).thenThrow(new ExternalScoreServiceException("Erro", 500));

            assertThrows(ExternalScoreServiceException.class, () -> useCase.execute(idValido));

            verify(findCustomerByIdRepository, times(1)).findById(idValido);
            verify(customerScoreClient, times(1)).getScoreByCpf(CPF_VALIDO);
        }
    }

    private void createCustomer() {
        customer = Customer.restore(
                new CustomerId(idValido),
                NOME_VALIDO,
                new Cpf(CPF_VALIDO),
                new Email(EMAIL_VALIDO),
                CustomerStatus.ACTIVE
        );
    }
}
