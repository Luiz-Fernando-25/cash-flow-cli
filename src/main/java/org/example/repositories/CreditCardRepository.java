package org.example.repositories;

import java.util.List;
import java.util.Optional;

import org.example.domain.models.CreditCard;

public interface CreditCardRepository {

    void save(CreditCard creditCard);

    Optional<CreditCard> findById(Integer id);

    List<CreditCard> findAll();

    void update(CreditCard creditCard);
    void delete(Integer id);
}
