package org.example.Repository;

import org.example.Entities.Sku;
import org.example.Entities.Variation;
import org.example.Entities.VariationOptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface VariationOptionsRepository extends JpaRepository<VariationOptions, Long> {
    Set<VariationOptions> findAllByVariation(Variation variation);
    Optional<VariationOptions> findByVariationOptionCode(String code);

}
