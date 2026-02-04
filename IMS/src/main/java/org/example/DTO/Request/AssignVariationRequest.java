package org.example.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class AssignVariationRequest {
    @NotNull(message = "Variation id cannot be blank")
    List<Long> variationIds;

    public AssignVariationRequest(List<Long> variationIds) {
        this.variationIds = variationIds;
    }

    public List<Long> getVariationIds() {
        return variationIds;
    }

    public void setVariationIds(List<Long> variationIds) {
        this.variationIds = variationIds;
    }
}
