package org.example.DTO.Response;

import org.example.DTO.Request.SaleItemRequest;

import java.time.LocalDate;
import java.util.List;

public class SalesResponse {
    private Long salesId;
    private CustomerResponse customer;
    private UserResponse staff;
    private Double salesAmount;
    private String salesPaymentStatus;
    private List<SaleItemResponse> salesItem;
    private LocalDate saleDate;

    public Long getSalesId() {
        return salesId;
    }

    public void setSalesId(Long salesId) {
        this.salesId = salesId;
    }

    public CustomerResponse getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerResponse customer) {
        this.customer = customer;
    }

    public UserResponse getStaff() {
        return staff;
    }

    public void setStaff(UserResponse staff) {
        this.staff = staff;
    }

    public Double getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(Double salesAmount) {
        this.salesAmount = salesAmount;
    }

    public String getSalesPaymentStatus() {
        return salesPaymentStatus;
    }

    public void setSalesPaymentStatus(String salesPaymentStatus) {
        this.salesPaymentStatus = salesPaymentStatus;
    }

    public List<SaleItemResponse> getSalesItem() {
        return salesItem;
    }

    public void setSalesItem(List<SaleItemResponse> salesItem) {
        this.salesItem = salesItem;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }
}
