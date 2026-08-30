package br.com.claudiocarige.desafio.application.dto;

import java.util.List;

public record CustomerPageDto(
        List<CustomerDto> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {

    public static CustomerPageDto of(
            List<CustomerDto> content,
            int page,
            int size,
            long totalElements) {

        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean hasNext = page + 1 < totalPages;
        boolean hasPrevious = page > 0 && totalPages > 0;

        return new CustomerPageDto(
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
