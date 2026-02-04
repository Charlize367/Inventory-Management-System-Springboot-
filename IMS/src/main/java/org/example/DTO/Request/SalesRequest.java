package org.example.DTO.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SalesRequest {

    private Long customerId;

    @NotNull(message = "User ID is required")
    private Long staffId;

    @NotNull(message = "Sale Items is required")
    private List<SaleItemRequest> salesItem;

    public SalesRequest(Long customerId, Long staffId, List<SaleItemRequest> salesItem) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.salesItem = salesItem;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }


    public List<SaleItemRequest> getSalesItem() {
        return salesItem;
    }

    public void setSalesItem(List<SaleItemRequest> salesItem) {
        this.salesItem = salesItem;
    }
}
