package org.example.Repository;


import org.example.Entities.Products;
import org.example.Entities.Sku;
import org.example.Entities.Variation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Products, Long> {
    List<Products> findProductByCategoriesCategoryId(Long categoryId);
    Products findByProductName(String productName);
    Page<Products> findByActiveTrue(Pageable pageable);
    List<Products> findAllByActiveTrue();
    List<Products> findAllByCategoriesCategoryIdAndActiveTrue(Long categoryId);
    Optional<Products> findByProductIdAndActiveTrue(Long id);
    Page<Products> findByCategoriesCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
    Long countByActiveTrue();
    List<Products> findByProductNameContainingIgnoreCase(String productName);
    Page<Products> findByBrandBrandIdAndActiveTrue(Long brandId, Pageable pageable);
    Page<Products> findByBrandBrandIdAndCategoriesCategoryIdAndActiveTrue(Long brandId, Long categoryId, Pageable pageable);
    List<Products> findAllByBrandBrandIdAndActiveTrue(Long brandId);
    List<Products> findByVariations(Variation variation);
    Products findBySku(Sku sku);


}
