package org.example.DTO.Response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.Entities.Sku;

public class VariationOptionsResponse {
    private Long variationId;
    private Long variationOptionId;
    private String variationOptionName;
    private String variationOptionCode;
    private Double variationPriceAdjustment;






    public Long getVariationId() {
        return variationId;
    }

    public void setVariationId(Long variationId) {
        this.variationId = variationId;
    }

    public Double getVariationPriceAdjustment() {
        return variationPriceAdjustment;
    }

    public void setVariationPriceAdjustment(Double variationPriceAdjustment) {
        this.variationPriceAdjustment = variationPriceAdjustment;
    }

    public Long getVariationOptionId() {
        return variationOptionId;
    }

    public void setVariationOptionId(Long variationOptionId) {
        this.variationOptionId = variationOptionId;
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


}
