package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.adapter.out.persistence.mapper.CustomerEntityMapper;
import br.com.claudiocarige.desafio.application.port.out.FindCustomerByIdRepositoryPort;
import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import br.com.claudiocarige.desafio.domain.exception.NotFoundException;
import br.com.claudiocarige.desafio.domain.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
public class FindCustomerByIdRepository implements FindCustomerByIdRepositoryPort {

    private static final String SQL_FIND_BY_ID =
            "SELECT id, name, cpf, email, status FROM customers WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public FindCustomerByIdRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Customer findById(UUID id) {
        log.info("### INICIANDO FindCustomerByIdRepository - ID: {} ###", id);
        List<CustomerEntity> result = jdbcTemplate.query(
                SQL_FIND_BY_ID,
                (rs, rowNum) -> new CustomerEntity(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("cpf"),
                        rs.getString("email"),
                        CustomerStatus.valueOf(rs.getString("status"))
                ),
                id.toString()
        );

        if (result.isEmpty()) {
            throw NotFoundException.of("Cliente não encontrado ", id);
        }
        log.info("### FINALIZANDO FindCustomerByIdRepository - Cliente encontrado: {} ###", result.get(0).getId());
        return CustomerEntityMapper.customerEntityToCustomer(result.get(0));
    }
}

