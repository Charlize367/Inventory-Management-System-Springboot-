package org.example.DTO.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.Entities.VariationOptions;

import java.util.List;

public class PurchaseItemRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer purchaseItemQuantity;

    private List<VariationOptionRequest> variationOptions;


    public PurchaseItemRequest(Long productId, Integer purchaseItemQuantity, List<VariationOptionRequest> variationOptions) {
        this.productId = productId;
        this.purchaseItemQuantity = purchaseItemQuantity;
        this.variationOptions = variationOptions;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public Integer getPurchaseItemQuantity() {
        return purchaseItemQuantity;
    }

    public void setPurchaseItemQuantity(Integer purchaseItemQuantity) {
        this.purchaseItemQuantity = purchaseItemQuantity;
    }

    public List<VariationOptionRequest> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(List<VariationOptionRequest> variationOptions) {
        this.variationOptions = variationOptions;
    }
}
