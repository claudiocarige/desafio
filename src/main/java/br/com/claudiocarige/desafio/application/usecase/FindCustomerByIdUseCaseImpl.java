package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerDto;
import br.com.claudiocarige.desafio.application.port.in.FindCustomerByIdUseCase;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import org.springframework.stereotype.Service;

@Service
public class FindCustomerByIdUseCaseImpl implements FindCustomerByIdUseCase {

    @Override
    public CustomerDto execute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }

        return new CustomerDto(id, "Cliente busca por id mockado", "12345678909", "cliente@email.com", CustomerStatus.ACTIVE);
    }
}
