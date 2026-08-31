package br.com.claudiocarige.desafio.adapter.out.score;

public record ScoreServiceResponse(
        String cpf,
        Integer score,
        String classification
) {
}
