package org.example.DTO.Response;

import org.example.DTO.Request.PurchaseItemRequest;
import org.example.Entities.Suppliers;
import org.example.Entities.Users;

import java.time.LocalDate;
import java.util.List;

public class PurchaseResponse {
    private Long purchaseId;
    private SupplierResponse supplier;
    private UserResponse staff;
    private Double purchaseAmount;
    private String purchaseStatus;
    private List<PurchaseItemResponse> purchaseItems;
    private LocalDate purchaseDate;


    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public SupplierResponse getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierResponse supplier) {
        this.supplier = supplier;
    }

    public UserResponse getStaff() {
        return staff;
    }

    public void setStaff(UserResponse staff) {
        this.staff = staff;
    }

    public Double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(Double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public String getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(String purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public List<PurchaseItemResponse> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseItemResponse> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
