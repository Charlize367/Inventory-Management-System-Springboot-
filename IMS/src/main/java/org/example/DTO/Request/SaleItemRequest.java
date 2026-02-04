package org.example.DTO.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.DTO.Response.ProductResponse;
import org.example.Entities.VariationOptions;

import java.util.List;

public class SaleItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer saleItemQuantity;

    private List<VariationOptionRequest> variationOptions;


    public SaleItemRequest(Long productId, Integer saleItemQuantity, List<VariationOptionRequest> variationOptions) {
        this.productId = productId;
        this.saleItemQuantity = saleItemQuantity;
        this.variationOptions = variationOptions;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getSaleItemQuantity() {
        return saleItemQuantity;
    }

    public void setSaleItemQuantity(Integer saleItemQuantity) {
        this.saleItemQuantity = saleItemQuantity;
    }

    public List<VariationOptionRequest> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(List<VariationOptionRequest> variationOptions) {
        this.variationOptions = variationOptions;
    }



}
