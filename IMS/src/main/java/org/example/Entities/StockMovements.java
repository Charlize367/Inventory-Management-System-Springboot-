package org.example.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Entity
public class StockMovements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockMovementId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "productId")
    @NotNull(message = "Product is required")
    private Products product;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity change must be at least 1")
    private Integer quantityChange;

    @NotNull(message = "Stock Movement is required")
    private StockMovement stockMovement;


    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum StockMovement {
        PURCHASE,
        SALE,
        ADJUSTMENT
    }

    @ManyToOne
    @JoinColumn(name = "userId")
    private Users staff;  // Who performed the change

    public Long getStockMovementId() {
        return stockMovementId;
    }

    public void setStockMovementId(Long stockMovementId) {
        this.stockMovementId = stockMovementId;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public StockMovement getStockMovement() {
        return stockMovement;
    }

    public void setStockMovement(StockMovement stockMovement) {
        this.stockMovement = stockMovement;
    }

    public Users getStaff() {
        return staff;
    }

    public void setStaff(Users staff) {
        this.staff = staff;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }



}
