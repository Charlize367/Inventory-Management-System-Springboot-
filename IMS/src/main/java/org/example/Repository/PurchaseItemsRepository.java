package org.example.Repository;

import org.example.DTO.Response.PurchaseItemResponse;
import org.example.Entities.PurchaseItems;
import org.example.Entities.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseItemsRepository extends JpaRepository<PurchaseItems, Long> {
    List<PurchaseItems> findByPurchase(Purchases purchases);
}
