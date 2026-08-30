package br.com.claudiocarige.desafio.adapter.out.persistence;

import br.com.claudiocarige.desafio.domain.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByCpf(String cpf);

    @Query(
        value = "SELECT * FROM customers ORDER BY id ASC",
        countQuery = "SELECT count(*) FROM customers",
        nativeQuery = true
    )
    Page<CustomerEntity> findAllCustomersNative(Pageable pageable);


}
