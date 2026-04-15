package org.example.repositories;

import java.util.List;
import java.util.Optional;

import org.example.domain.models.Transfer;

public interface TransferRepository {
    void save(Transfer transfer);

    Optional<Transfer> findById(Integer id);

    List<Transfer> findAll();

    void update(Transfer transfer);
    void delete(Integer id);
}
