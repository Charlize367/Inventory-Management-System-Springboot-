package org.example.DTO.Request;

import jakarta.validation.constraints.NotNull;

public class SkuRequest {

    @NotNull(message = "Sku Status is required")
    private boolean active;

    public boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
