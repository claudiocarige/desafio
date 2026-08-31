package br.com.claudiocarige.desafio.application.dto;

public record CustomerScoreDto(
        String cpf,
        Integer score,
        String classification
) {
}
