package org.example.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EditSkuCodeRequest {

    @NotBlank(message = "Sku code cannot be blank")
    @Size(max = 15, message = "Sku must have at least 1-15 characters")
    private String skuCode;

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
}
