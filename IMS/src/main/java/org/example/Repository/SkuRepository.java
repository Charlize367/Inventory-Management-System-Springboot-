package org.example.Repository;

import org.example.DTO.Request.VariationOptionRequest;
import org.example.Entities.Products;
import org.example.Entities.Sku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkuRepository extends JpaRepository<Sku, Long> {
    Sku getBySkuCode(String skuCode);
    Optional<Sku> findBySkuCode(String skuCode);
    List<Sku> findByProduct(Products product);
    List<Sku> findAllByProduct(Products product);
    List<Sku> findByProductAndVariationOptionsEmpty(Products product);
    List<Sku> findByVariationOptions_VariationOptionId(Long variationOptionId);
    List<Sku> findByVariationOptions(List<VariationOptionRequest> variationOptions);

}
