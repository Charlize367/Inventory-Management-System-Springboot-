package org.example.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateVariationNameRequest {

    @NotBlank(message = "Variation name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must have at least 1-50 characters")
    private String variationName;

    public UpdateVariationNameRequest(String variationName) {
        this.variationName = variationName;
    }

    public String getVariationName() {
        return variationName;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

}
