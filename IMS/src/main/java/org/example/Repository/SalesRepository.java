package org.example.Repository;

import org.example.Entities.Purchases;
import org.example.Entities.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public  interface SalesRepository extends JpaRepository<Sales, Long> {
    Page<Sales> findSalesByStaff_UserId(Long userId, Pageable pageable);
}
