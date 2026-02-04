package org.example.DTO.Request;

import jakarta.validation.constraints.NotNull;

public class SalesPaymentStatusRequest {
    @NotNull(message = "Sales Payment Status is required")
    private String salesPaymentStatus;

    public SalesPaymentStatusRequest(String salesPaymentStatus) {
        this.salesPaymentStatus = salesPaymentStatus;
    }

    public String getSalesPaymentStatus() {
        return salesPaymentStatus;
    }

    public void setSalesPaymentStatus(String salesPaymentStatus) {
        this.salesPaymentStatus = salesPaymentStatus;
    }
}
