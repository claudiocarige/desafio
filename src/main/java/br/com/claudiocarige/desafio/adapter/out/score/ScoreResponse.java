package br.com.claudiocarige.desafio.adapter.out.score;

public record ScoreResponse(
        String cpf,
        Integer score,
        String classification
) {
}