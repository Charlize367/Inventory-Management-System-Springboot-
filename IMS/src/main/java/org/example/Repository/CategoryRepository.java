package org.example.Repository;

import org.example.Entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Categories, Long> {
    Categories findByCategoryName(String categoryName);
}
