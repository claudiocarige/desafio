package br.com.claudiocarige.desafio.adapter.out.score;

import br.com.claudiocarige.desafio.application.dto.CustomerScoreDto;
import br.com.claudiocarige.desafio.domain.exception.ExternalScoreServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CustomerScoreClient - Testes do adapter HTTP de Score")
class CustomerScoreClientTest {

    private static final String CPF_VALIDO = "85541025095";

    private WebClient webClientMock;
    private WebClient.RequestHeadersUriSpec uriSpecMock;
    private WebClient.RequestHeadersSpec headersSpecMock;
    private WebClient.ResponseSpec responseSpecMock;

    private CustomerScoreClient client;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        webClientMock = mock(WebClient.class);
        uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(webClientMock.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(anyString(), anyString())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);

        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);

        client = new CustomerScoreClient(webClientMock);
    }

    @Nested
    @DisplayName("Caminho Feliz")
    class CaminhoFeliz {

        @Test
        @DisplayName("Deve consultar o score com sucesso via WebClient e retornar DTO")
        void deveRetornarScoreComSucesso() {
            ScoreResponse scoreResponseMock = new ScoreResponse(CPF_VALIDO, 755, "Bom");
            when(responseSpecMock.bodyToMono(ScoreResponse.class)).thenReturn(Mono.just(scoreResponseMock));

            CustomerScoreDto resultado = client.getScoreByCpf(CPF_VALIDO);

            assertNotNull(resultado);
            assertEquals(CPF_VALIDO, resultado.cpf());
            assertEquals(755, resultado.score());
            assertEquals("Bom", resultado.classification());
        }
    }

    @Nested
    @DisplayName("Validação de Entrada")
    class ValidacaoEntrada {

        @Test
        @DisplayName("Deve lançar IllegalArgumentException se o CPF for nulo")
        void deveLancarExcecaoSeCpfNulo() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> client.getScoreByCpf(null));
            assertTrue(ex.getMessage().contains("obrigatório"));
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException se o CPF for vazio")
        void deveLancarExcecaoSeCpfVazio() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> client.getScoreByCpf("   "));
            assertTrue(ex.getMessage().contains("obrigatório"));
        }
    }

    @Nested
    @DisplayName("Tratamento de Exceções do Serviço Externo")
    class TratamentoErrosWebClient {

        @Test
        @DisplayName("Deve lançar ExternalScoreServiceException quando resposta não trouxer dados mínimos esperados")
        void deveLancarExcecaoSeCorpoVazioOuFaltandoDados() {
            ScoreResponse responseIncompleto = new ScoreResponse(null, null, null);
            when(responseSpecMock.bodyToMono(ScoreResponse.class)).thenReturn(Mono.just(responseIncompleto));

            ExternalScoreServiceException ex = assertThrows(ExternalScoreServiceException.class, () -> client.getScoreByCpf(CPF_VALIDO));

            assertTrue(ex.getMessage().contains("Resposta inesperada"));
        }

        @Test
        @DisplayName("Deve envelopar WebClientResponseException genérica como ExternalScoreServiceException")
        void deveEnveloparWebClientResponseException() {
            WebClientResponseException mockException = mock(WebClientResponseException.class);
            when(mockException.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.BAD_REQUEST);
            when(mockException.getResponseBodyAsString()).thenReturn("Erro generico");

            when(responseSpecMock.bodyToMono(ScoreResponse.class)).thenThrow(mockException);

            ExternalScoreServiceException ex = assertThrows(ExternalScoreServiceException.class, () -> client.getScoreByCpf(CPF_VALIDO));

            assertTrue(ex.getMessage().contains("Falha ao consultar"));
            assertTrue(ex.getMessage().contains("400 BAD_REQUEST"));
        }

        @Test
        @DisplayName("Deve capturar e converter Exception genérica em ExternalScoreServiceException")
        void deveTratarQualquerExcecaoLancadaPeloWebClient() {
            when(responseSpecMock.bodyToMono(ScoreResponse.class)).thenThrow(new RuntimeException("Timeout do servidor"));

            ExternalScoreServiceException ex = assertThrows(ExternalScoreServiceException.class, () -> client.getScoreByCpf(CPF_VALIDO));

            assertTrue(ex.getMessage().contains("Não foi possível consultar"));
            assertEquals("Timeout do servidor", ex.getCause().getMessage());
        }
    }
}
