package org.example.DTO.Request;

import jakarta.validation.constraints.NotNull;

public class PurchaseStatusRequest {
    @NotNull(message = "Purchase Status is required")
    private String purchaseStatus;

    public PurchaseStatusRequest(String purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public String getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(String purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }
}
