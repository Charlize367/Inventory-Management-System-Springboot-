package org.example.DTO.Response;

import java.util.List;
import java.util.Set;

public class SaleItemResponse {
    private Long saleItemId;
    private ProductResponse product;
    private Integer saleItemQuantity;
    private Double salePrice;
    private Set<VariationOptionsResponse> variationOption;



    public Set<VariationOptionsResponse> getVariationOption() {
        return variationOption;
    }

    public void setVariationOption(Set<VariationOptionsResponse> variationOption) {
        this.variationOption = variationOption;
    }



    public Long getSaleItemId() {
        return saleItemId;
    }

    public void setSaleItemId(Long saleItemId) {
        this.saleItemId = saleItemId;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public Integer getSaleItemQuantity() {
        return saleItemQuantity;
    }

    public void setSaleItemQuantity(Integer saleItemQuantity) {
        this.saleItemQuantity = saleItemQuantity;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }
}
