package org.example.DTO.Request;

import org.example.Entities.Products;
import org.example.Entities.StockMovements;
import org.example.Entities.Users;

import java.time.LocalDateTime;

public class StockMovementRequest {


    private String stockMovement;



    public String getStockMovement() {
        return stockMovement;
    }

    public void setStockMovement(String stockMovement) {
        this.stockMovement = stockMovement;
    }



}
