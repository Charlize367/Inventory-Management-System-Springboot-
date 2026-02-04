package org.example.Repository;


import org.example.Entities.Products;
import org.example.Entities.Purchases;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface PurchaseRepository extends JpaRepository<Purchases, Long> {
    Page<Purchases> findPurchasesByStaff_UserId(Long userId, Pageable pageable);
}
