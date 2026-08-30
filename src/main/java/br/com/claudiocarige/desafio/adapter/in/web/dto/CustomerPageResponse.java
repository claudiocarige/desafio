package br.com.claudiocarige.desafio.adapter.in.web.dto;

import java.util.List;

public record CustomerPageResponse(
        List<CustomerResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious) {

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
