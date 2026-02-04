package org.example.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
public class Purchases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "supplierId")
    @NotNull(message = "Supplier is required")
    private Suppliers supplier;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "userId")
    @NotNull(message = "Staff is required")
    private Users staff;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Amount must not exceed 1,000,000")
    private Double purchaseAmount;

    @NotNull(message = "Purchase status is required")
    private PurchaseStatus purchaseStatus;

    public Purchases() {

    }
    public enum PurchaseStatus {
        PENDING,
        COMPLETE,
        CANCELLED
    }

    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    @OneToMany(mappedBy = "purchase", fetch=FetchType.EAGER,  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItems> purchaseItems;

    @PrePersist
    public void prePersist() {
        if (purchaseAmount == null) {
            purchaseAmount = 0.01;
        }
        if (purchaseDate == null) {
            purchaseDate = LocalDate.now();
        }
    }

    public Purchases(Long purchaseId, Suppliers supplier, Users staff, Double purchaseAmount, PurchaseStatus purchaseStatus, LocalDate purchaseDate) {
        this.purchaseId = purchaseId;
        this.supplier = supplier;
        this.staff = staff;
        this.purchaseAmount = purchaseAmount;
        this.purchaseStatus = purchaseStatus;
        this.purchaseDate = purchaseDate;
    }


    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Suppliers getSupplier() {
        return supplier;
    }

    public void setSupplier(Suppliers supplier) {
        this.supplier = supplier;
    }

    public Users getStaff() {
        return staff;
    }

    public void setStaff(Users staff) {
        this.staff = staff;
    }

    public Double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(Double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public PurchaseStatus getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(PurchaseStatus purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Purchases purchases = (Purchases) o;
        return Objects.equals(purchaseId, purchases.purchaseId) && Objects.equals(supplier, purchases.supplier) && Objects.equals(staff, purchases.staff) && Objects.equals(purchaseAmount, purchases.purchaseAmount) && purchaseStatus == purchases.purchaseStatus && Objects.equals(purchaseDate, purchases.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId, supplier, staff, purchaseAmount, purchaseStatus, purchaseDate);
    }


}
