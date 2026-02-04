package org.example.DTO.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateVarOptionRequest {
    @NotBlank(message = "Variation option name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must have at least 1-50 characters")
    private String variationOptionName;

    @NotNull(message = "Price is required")
    @DecimalMax(value = "1000000.00", message = "Price must not exceed 1,000,000")
    private Double variationPriceAdjustment;

    public UpdateVarOptionRequest(String variationOptionName) {
        this.variationOptionName = variationOptionName;
    }

    public String getVariationOptionName() {
        return variationOptionName;
    }

    public void setVariationOptionName(String variationOptionName) {
        this.variationOptionName = variationOptionName;
    }


    public Double getVariationPriceAdjustment() {
        return variationPriceAdjustment;
    }

    public void setVariationPriceAdjustment(Double variationPriceAdjustment) {
        this.variationPriceAdjustment = variationPriceAdjustment;
    }
}
