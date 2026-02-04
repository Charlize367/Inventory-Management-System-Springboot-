package org.example.Repository;

import org.example.Entities.Categories;
import org.example.Entities.Variation;
import org.example.Entities.VariationOptions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariationRepository extends JpaRepository<Variation, Long> {

    Variation findByVariationOptions(VariationOptions variationOptions);

}

