package org.example.Repository;

import org.example.Entities.StockMovements;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementsRepository extends JpaRepository<StockMovements, Long> {

}