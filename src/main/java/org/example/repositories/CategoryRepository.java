package org.example.repositories;

import java.util.List;
import java.util.Optional;
import org.example.domain.models.Category;

public interface CategoryRepository {
    void save(Category category);

    Optional<Category> findById(Integer id);

    List<Category> findAll();

    void update(Category category);
    void delete(Integer id);
}
