package org.example.DTO.Response;

import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import org.example.Entities.Products;

import java.util.List;

public class SkuResponse {
    private Long skuId;
    private String skuCode;
    private ProductResponse product;
    private Double finalPrice;
    private Integer stockQuantity;
    private boolean isActive;
    private List<VariationOptionsResponse> variationOptions;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
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

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<VariationOptionsResponse> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(List<VariationOptionsResponse> variationOptions) {
        this.variationOptions = variationOptions;
    }


}
