package org.example.Entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salesId;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "customerId", nullable = true)
    @Nullable
    private Customers customer;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "userId")
    @NotNull(message = "Staff is required")
    private Users staff;


    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Amount must not exceed 1,000,000")
    private Double salesAmount;

    @NotNull(message = "Sales Payment status is required")
    private SalesPaymentStatus salesPaymentStatus;


    @NotNull(message = "Sale date is required")
    @PastOrPresent(message = "Sale date cannot be in the future")
    private LocalDate saleDate;




    @OneToMany(mappedBy = "sales", fetch=FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItems> saleItems;


    @PrePersist
    public void prePersist() {
        if (salesAmount == null) {
            salesAmount = 0.01;
        }
        if (saleDate == null) {
            saleDate = LocalDate.now();
        }
    }

    public Sales(Long salesId, Customers customer, Users staff, Double salesAmount, SalesPaymentStatus salesPaymentStatus, LocalDate saleDate) {
        this.salesId = salesId;
        this.customer = customer;
        this.staff = staff;
        this.salesAmount = salesAmount;
        this.salesPaymentStatus = salesPaymentStatus;
        this.saleDate = saleDate;
    }

    public enum SalesPaymentStatus {
        PENDING,
        PROCESSING,
        PAID,
        FAILED,
        CANCELLED,
        REFUNDED,
        PARTIALLY_REFUNDED,
        DISPUTED,
        EXPIRED
    }

    public Sales(){}

    public Long getSalesId() {
        return salesId;
    }

    public void setSalesId(Long salesId) {
        this.salesId = salesId;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public Users getStaff() {
        return staff;
    }

    public void setStaff(Users staff) {
        this.staff = staff;
    }

    public Double getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(Double salesAmount) {
        this.salesAmount = salesAmount;
    }

    public SalesPaymentStatus getSalesPaymentStatus() {
        return salesPaymentStatus;
    }

    public void setSalesPaymentStatus(SalesPaymentStatus salesPaymentStatus) {
        this.salesPaymentStatus = salesPaymentStatus;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sales sales = (Sales) o;
        return Objects.equals(salesId, sales.salesId) && Objects.equals(customer, sales.customer) && Objects.equals(staff, sales.staff) && Objects.equals(salesAmount, sales.salesAmount) && Objects.equals(salesPaymentStatus, sales.salesPaymentStatus) && Objects.equals(saleDate, sales.saleDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salesId, customer, staff, salesAmount, salesPaymentStatus, saleDate);
    }



}
