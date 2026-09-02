package br.com.claudiocarige.desafio.adapter.out.score;

import br.com.claudiocarige.desafio.adapter.out.score.exceptions.ExternalErrorResponse;
import br.com.claudiocarige.desafio.application.dto.CustomerScoreDto;
import br.com.claudiocarige.desafio.application.port.out.CustomerScoreClientPort;
import br.com.claudiocarige.desafio.domain.exception.ExternalScoreServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomerScoreClient implements CustomerScoreClientPort {

    private final WebClient webClient;

    public CustomerScoreClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public CustomerScoreDto getScoreByCpf(String cpf) {
        log.info("### INICIANDO CustomerScoreClient.getScoreByCpf - CPF: {} ###", cpf);

        if (cpf == null || cpf.isBlank()) {
            log.error("XXX Error - CPF nulo ou em branco para consulta de score XXX");
            throw new IllegalArgumentException("O CPF Cliente esta nulo ou em branco, e ele é obrigatório para consultar o score.");
        }

        try {
            ScoreResponse response = webClient.get()
                    .uri("/scores/{cpf}", cpf)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(ExternalErrorResponse.class)
                                    .flatMap(body -> {
                                        log.error("XXX Error 4xx ao consultar score. cpf={}, status={}, body={} XXX",
                                                cpf, clientResponse.statusCode().value(), body);
                                        return Mono.error(new ExternalScoreServiceException(
                                                "Serviço externo recusou a consulta: " + body.response(), body.status()));
                                    })
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            clientResponse.bodyToMono(ExternalErrorResponse.class)
                                    .flatMap(body -> {
                                        log.error("XXX Error - Serviço de score indisponível. cpf={}, status={}, body={} XXX",
                                                cpf, clientResponse.statusCode().value(), body);
                                        return Mono.error(new ExternalScoreServiceException(
                                                "Serviço externo de score indisponível: " + body.response(), body.status()));
                                    })
                    )
                    .bodyToMono(ScoreResponse.class)
                    .block();

            if (response == null || response.score() == null || response.classification() == null) {
                log.error("XXX Error - Resposta inesperada do serviço externo de score. cpf={} XXX", cpf);
                throw new ExternalScoreServiceException(
                        "Resposta inesperada do serviço externo de score para o CPF %s.".formatted(cpf), 500);
            }

            CustomerScoreDto scoreDto = new CustomerScoreDto(cpf, response.score(), response.classification());

            log.info("### FINALIZANDO CustomerScoreClient.getScoreByCpf - CPF: {}, Score: {} ###", cpf, scoreDto.score());
            return scoreDto;

        } catch (WebClientResponseException ex) {
            log.error("XXX Error - Falha ao consultar o score do cliente no serviço externo. Status: {}. Corpo: {} XXX",
                    ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ExternalScoreServiceException(
                    "Falha ao consultar o score do cliente no serviço externo. Status: %s. Corpo: %s"
                            .formatted(ex.getStatusCode(), ex.getResponseBodyAsString()), ex);
        } catch (ExternalScoreServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("XXX Error - Não foi possível consultar o score do cliente no serviço externo. cpf={} XXX", cpf, ex);
            throw new ExternalScoreServiceException(
                    "Não foi possível consultar o score do cliente no serviço externo.", ex);
        }
    }

}
