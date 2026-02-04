package org.example.Repository;

import org.example.DTO.Response.SaleItemResponse;
import org.example.Entities.SaleItems;
import org.example.Entities.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SaleItemsRepository extends JpaRepository<SaleItems, Long> {
    List<SaleItems> findBySales(Sales sales);


}
