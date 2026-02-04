package org.example.DTO.Request;

import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.*;
import org.example.Entities.Products;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VariationRequest {
    @NotBlank(message = "Variation name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must have at least 1-50 characters")
    private String variationName;

    @NotNull(message = "Variation options is required")
    private List<VariationOptionRequest> variationOptions;



    public VariationRequest(String variationName, List<VariationOptionRequest> variationOptions) {
        this.variationName = variationName;
        this.variationOptions = variationOptions;
    }

    public String getVariationName() {
        return variationName;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public List<VariationOptionRequest> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(List<VariationOptionRequest> variationOptions) {
        this.variationOptions = variationOptions;
    }


}
