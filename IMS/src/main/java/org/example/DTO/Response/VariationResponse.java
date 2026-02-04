package org.example.DTO.Response;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.Entities.VariationOptions;

import java.util.List;

public class VariationResponse {
    private Long variationId;
    private String variationName;
    private List<VariationOptionsResponse> variationOptions;



    public Long getVariationId() {
        return variationId;
    }

    public void setVariationId(Long variationId) {
        this.variationId = variationId;
    }

    public String getVariationName() {
        return variationName;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public List<VariationOptionsResponse> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(List<VariationOptionsResponse> variationOptions) {
        this.variationOptions = variationOptions;
    }
}
