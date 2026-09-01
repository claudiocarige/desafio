package br.com.claudiocarige.desafio.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CustomerPageResponse(
        @Schema(description = "Clientes da página atual") List<CustomerResponse> content,
        @Schema(description = "Número da página atual (0-based)", example = "0") int page,
        @Schema(description = "Tamanho da página", example = "20") int size,
        @Schema(description = "Total de elementos encontrados", example = "42") long totalElements,
        @Schema(description = "Total de páginas", example = "3") int totalPages,
        @Schema(description = "Indica se existe próxima página") boolean hasNext,
        @Schema(description = "Indica se existe página anterior") boolean hasPrevious) {

    public static CustomerPageResponse of(
            List<CustomerResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious) {

        return new CustomerPageResponse(
                content,
                page,
                size,
                totalElements,
                totalPages,
                hasNext,
                hasPrevious
        );
    }
}
