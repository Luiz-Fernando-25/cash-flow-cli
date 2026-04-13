package org.example.repositories;

import java.util.List;
import java.util.Optional;

import org.example.domain.models.AbstractAccount;

public interface AccountRepository {
    void save(AbstractAccount account);

    Optional<AbstractAccount> findById(Integer id);

    List<AbstractAccount> findAll();

    void update(AbstractAccount account);
    void delete(Integer id);
    
}
