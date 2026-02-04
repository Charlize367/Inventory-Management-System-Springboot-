package org.example.DTO.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PurchaseRequest {
    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Staff ID is required")
    private Long staffId;


    @NotNull(message = "Purchase Items is required")
    private List<PurchaseItemRequest> purchaseItems;


    public PurchaseRequest(Long supplierId, Long staffId, List<PurchaseItemRequest> purchaseItems) {
        this.supplierId = supplierId;
        this.staffId = staffId;
        this.purchaseItems = purchaseItems;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }



    public List<PurchaseItemRequest> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseItemRequest> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }
}
