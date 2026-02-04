package org.example.DTO.Response;

import org.example.Entities.Products;
import org.example.Entities.StockMovements;
import org.example.Entities.Users;

import java.time.LocalDateTime;

public class StockMovementResponse {
    private Long stockMovementId;
    private ProductResponse product;
    private Integer quantityChange;
    private String stockMovement;
    private LocalDateTime createdAt;
    private UserResponse staff;

    public String getStockMovement() {
        return stockMovement;
    }

    public void setStockMovement(String stockMovement) {
        this.stockMovement = stockMovement;
    }

    public Long getStockMovementId() {
        return stockMovementId;
    }

    public void setStockMovementId(Long stockMovementId) {
        this.stockMovementId = stockMovementId;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserResponse getStaff() {
        return staff;
    }

    public void setStaff(UserResponse staff) {
        this.staff = staff;
    }

}
