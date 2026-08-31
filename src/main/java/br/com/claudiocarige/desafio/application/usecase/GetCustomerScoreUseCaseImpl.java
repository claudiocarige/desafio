package br.com.claudiocarige.desafio.application.usecase;

import br.com.claudiocarige.desafio.application.dto.CustomerScoreDto;
import br.com.claudiocarige.desafio.application.port.in.GetCustomerScoreUseCase;
import br.com.claudiocarige.desafio.application.port.out.CustomerScoreClientPort;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.domain.exception.DomainException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCustomerScoreUseCaseImpl implements GetCustomerScoreUseCase {

    private final FindCustomerByIdRepositoryPort findCustomerByIdRepository;
    private final CustomerScoreClientPort customerScoreClient;

    public GetCustomerScoreUseCaseImpl(
            FindCustomerByIdRepositoryPort findCustomerByIdRepository,
            CustomerScoreClientPort customerScoreClient) {
        this.findCustomerByIdRepository = findCustomerByIdRepository;
        this.customerScoreClient = customerScoreClient;
    }

    @Override
    public CustomerScoreDto execute(UUID customerId) {
        if (customerId == null || customerId.toString().isBlank()) {
            throw DomainException.with("ID do cliente é obrigatório");
        }

        Customer customer = findCustomerByIdRepository.findById(customerId);
        CustomerScoreDto customerScore = customerScoreClient.getScoreByCpf(customer.getCpf().value());
        return customerScore;
    }
}
