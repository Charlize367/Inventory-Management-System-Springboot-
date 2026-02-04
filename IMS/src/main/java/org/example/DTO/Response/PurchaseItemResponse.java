package org.example.DTO.Response;

import org.example.Entities.Products;

import java.util.List;
import java.util.Set;

public class PurchaseItemResponse {
    private Long purchaseItemId;
    private ProductResponse product;
    private Integer purchaseItemQuantity;
    private Double purchaseCostPrice;
    private Set<VariationOptionsResponse> variationOptions;


    public Set<VariationOptionsResponse> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(Set<VariationOptionsResponse> variationOptions) {
        this.variationOptions = variationOptions;
    }

    public Long getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(Long purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public Integer getPurchaseItemQuantity() {
        return purchaseItemQuantity;
    }

    public void setPurchaseItemQuantity(Integer purchaseItemQuantity) {
        this.purchaseItemQuantity = purchaseItemQuantity;
    }

    public Double getPurchaseCostPrice() {
        return purchaseCostPrice;
    }

    public void setPurchaseCostPrice(Double purchaseCostPrice) {
        this.purchaseCostPrice = purchaseCostPrice;
    }
}
