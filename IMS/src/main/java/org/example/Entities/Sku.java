package org.example.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

@Entity
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skuId;

    @NotBlank(message = "Sku code cannot be blank")
    @Size(max = 15, message = "Sku must have at least 1-15 characters")
    private String skuCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    @NotNull(message = "Product is required")
    private Products product;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Price must not exceed 1,000,000")
    private Double finalPrice;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be at least 0")
    private Integer stockQuantity;

    private boolean active = true;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sku_variation_options",
            joinColumns = @JoinColumn(name = "skuId"),
            inverseJoinColumns = @JoinColumn(name = "variationOptionId")
    )
    private List<VariationOptions> variationOptions;



    public Sku(){}



    public Sku(Long skuId, String skuCode,  Products product, Double finalPrice, Integer stockQuantity, boolean active) {
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.product = product;
        this.finalPrice = finalPrice;
        this.stockQuantity = stockQuantity;
        this.active = active;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<VariationOptions> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(List<VariationOptions> variationOptions) {
        this.variationOptions = variationOptions;
    }
}
