package org.example.repositories;

import java.util.List;
import java.util.Optional;

import org.example.domain.models.AbstractTransaction;

public interface TransactionRepository {
    void save(AbstractTransaction transaction);

    Optional<AbstractTransaction> findById(Integer id);

    List<AbstractTransaction> findAll();

    void update(AbstractTransaction transaction);
    void delete(Integer id);
}
