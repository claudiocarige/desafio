package br.com.claudiocarige.desafio.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(
        value = "SELECT * FROM customers WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))",
        countQuery = "SELECT count(*) FROM customers WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))",
        nativeQuery = true
    )
    Page<CustomerEntity> findByNameContainingNative(@Param("name") String name, Pageable pageable);
}
