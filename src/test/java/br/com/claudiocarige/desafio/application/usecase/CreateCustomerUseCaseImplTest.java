package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CreateCustomerDto;
import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.port.out.CreateCustomerRepositoryPort;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CreateCustomerUseCaseImpl - Testes de orquestração do use case de criação")
class CreateCustomerUseCaseImplTest {

    private static final String CPF_VALIDO = "85541025095";
    private static final String EMAIL_VALIDO = "joao@email.com";
    private static final String NOME_VALIDO = "João Silva";

    private CreateCustomerRepositoryPort createCustomerRepository;
    private CreateCustomerUseCaseImpl useCase;

    private Customer clienteSalvo;
    private CreateCustomerDto dto;

    @BeforeEach
    void setUp() {
        createCustomerRepository = mock(CreateCustomerRepositoryPort.class);
        useCase = new CreateCustomerUseCaseImpl(createCustomerRepository);

        createClienteSalvo();

        createCustomerDto();
    }

    @Nested
    @DisplayName("Caminho feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("Deve criar cliente com sucesso e retornar CustomerDto com dados corretos")
        void deveCriarClienteComSucesso() {
            when(createCustomerRepository.save(any(Customer.class))).thenReturn(clienteSalvo);

            CustomerDto resultado = useCase.execute(dto);

            assertNotNull(resultado.id());
            assertEquals(NOME_VALIDO, resultado.name());
            assertEquals(CPF_VALIDO, resultado.cpf());
            assertEquals(EMAIL_VALIDO, resultado.email());
            assertEquals(CustomerStatus.ACTIVE, resultado.status());
            verify(createCustomerRepository, times(1)).save(any(Customer.class));
        }

        @Test
        @DisplayName("Deve garantir que o status do cliente criado é sempre ACTIVE")
        void deveRetornarClienteComStatusActiveSempreNaCriacao() {
            when(createCustomerRepository.save(any(Customer.class))).thenReturn(clienteSalvo);

            CustomerDto resultado = useCase.execute(dto);

            assertEquals(CustomerStatus.ACTIVE, resultado.status());
        }
    }

    @Nested
    @DisplayName("Regras de negócio - CPF duplicado")
    class CpfDuplicado {

        @Test
        @DisplayName("Deve propagar DomainException quando CPF já está cadastrado")
        void devePropagaDomainExceptionQuandoCpfJaCadastrado() {
            when(createCustomerRepository.save(any(Customer.class)))
                    .thenThrow(DomainException.with("CPF already exists"));

            DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(dto));

            assertTrue(ex.getMessage().contains("CPF"));
            verify(createCustomerRepository, times(1)).save(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("Validações de entrada - repositório não deve ser chamado")
    class ValidacoesDeEntrada {

        @Test
        @DisplayName("Deve lançar exceção para CPF com todos os dígitos iguais antes de chamar repositório")
        void deveLancarExcecaoQuandoCpfInvalidoAntesDeChamarRepositorio() {
            assertThrows(DomainException.class, () -> useCase.execute(
                    new CreateCustomerDto(NOME_VALIDO, new Cpf("11111111111"), new Email(EMAIL_VALIDO))));

            verifyNoInteractions(createCustomerRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção para CPF com dígitos verificadores errados antes de chamar repositório")
        void deveLancarExcecaoQuandoCpfComDigitosVerificadoresErrados() {
            assertThrows(DomainException.class, () -> useCase.execute(
                    new CreateCustomerDto(NOME_VALIDO, new Cpf("12345678900"), new Email(EMAIL_VALIDO))));

            verifyNoInteractions(createCustomerRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção para e-mail sem @ antes de chamar repositório")
        void deveLancarExcecaoQuandoEmailInvalidoAntesDeChamarRepositorio() {
            assertThrows(DomainException.class, () -> useCase.execute(
                    new CreateCustomerDto(NOME_VALIDO, new Cpf(CPF_VALIDO), new Email("emailinvalido"))));

            verifyNoInteractions(createCustomerRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção para nome nulo antes de chamar repositório")
        void deveLancarExcecaoQuandoNomeNuloAntesDeChamarRepositorio() {
            DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(
                    new CreateCustomerDto(null, new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO))));

            assertTrue(ex.getMessage().contains("Nome"));
            verifyNoInteractions(createCustomerRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção para nome em branco antes de chamar repositório")
        void deveLancarExcecaoQuandoNomeEmBrancoAntesDeChamarRepositorio() {
            DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(
                    new CreateCustomerDto("  ", new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO))));

            assertTrue(ex.getMessage().contains("Nome"));
            verifyNoInteractions(createCustomerRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção para nome com mais de 255 caracteres antes de chamar repositório")
        void deveLancarExcecaoQuandoNomeUltrapassaLimiteAntesDeChamarRepositorio() {
            String nomeGrande = "A".repeat(256);

            DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(
                    new CreateCustomerDto(nomeGrande, new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO))));

            assertTrue(ex.getMessage().contains("255"));
            verifyNoInteractions(createCustomerRepository);
        }
    }

    private void createCustomerDto() {
        dto = new CreateCustomerDto(NOME_VALIDO, new Cpf(CPF_VALIDO), new Email(EMAIL_VALIDO));
    }

    private void createClienteSalvo() {
        clienteSalvo = Customer.restore(
                new CustomerId(UUID.randomUUID()),
                NOME_VALIDO,
                new Cpf(CPF_VALIDO),
                new Email(EMAIL_VALIDO),
                CustomerStatus.ACTIVE
        );
    }
}
