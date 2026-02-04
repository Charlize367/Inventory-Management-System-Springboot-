package org.example.DTO.Request;

import jakarta.validation.constraints.*;

public class VariationOptionRequest {

    @NotBlank(message = "Variation option name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must have at least 1-50 characters")
    private String variationOptionName;

    @NotBlank(message = "Variation option code cannot be blank")
    @Size(max = 5, message = "Code name must have at least 1-5 characters")
    private String variationOptionCode;


    @NotNull(message = "Price is required")
    @DecimalMax(value = "1000000.00", message = "Price must not exceed 1,000,000")
    private Double variationPriceAdjustment;

    public VariationOptionRequest(String variationOptionName) {
        this.variationOptionName = variationOptionName;
    }

    public String getVariationOptionName() {
        return variationOptionName;
    }

    public void setVariationOptionName(String variationOptionName) {
        this.variationOptionName = variationOptionName;
    }

    public String getVariationOptionCode() {
        return variationOptionCode;
    }

    public void setVariationOptionCode(String variationOptionCode) {
        this.variationOptionCode = variationOptionCode;
    }

    public Double getVariationPriceAdjustment() {
        return variationPriceAdjustment;
    }

    public void setVariationPriceAdjustment(Double variationPriceAdjustment) {
        this.variationPriceAdjustment = variationPriceAdjustment;
    }
}
