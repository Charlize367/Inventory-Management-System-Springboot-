package org.example.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.*;

@Entity
public class PurchaseItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseItemsId;

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name = "purchaseId")
    @NotNull(message = "Purchase is required")
    private Purchases purchase;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "productId")
    @NotNull(message = "Product is required")
    private Products product;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer purchaseItemQuantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Price must not exceed 1,000,000")
    private Double purchaseCostPrice;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "purchase_item_variation_options",
            joinColumns = @JoinColumn(name = "variationOptionId"),
            inverseJoinColumns = @JoinColumn(name = "purchaseItemsId")
    )
    private Set<VariationOptions> variationOptions = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String variationOptionNames;

    public PurchaseItems(Long purchaseItemsId, Purchases purchase, Products product, Integer purchaseItemQuantity, Double purchaseCostPrice) {
        this.purchaseItemsId = purchaseItemsId;
        this.purchase = purchase;
        this.product = product;
        this.purchaseItemQuantity = purchaseItemQuantity;
        this.purchaseCostPrice = purchaseCostPrice;
    }

    public PurchaseItems() {

    }

    public Long getPurchaseItemsId() {
        return purchaseItemsId;
    }

    public void setPurchaseItemsId(Long purchaseItemsId) {
        this.purchaseItemsId = purchaseItemsId;
    }

    public Purchases getPurchase() {
        return purchase;
    }

    public void setPurchases(Purchases purchase) {
        this.purchase = purchase;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Integer getPurchaseItemQuantity() {
        return purchaseItemQuantity;
    }

    public void setPurchaseItemQuantity(Integer purchaseItemQuantity) {
        this.purchaseItemQuantity = purchaseItemQuantity;
    }

    public Double getPurchaseCostPrice() {
        return purchaseCostPrice;
    }

    public void setPurchaseCostPrice(Double purchaseCostPrice) {
        this.purchaseCostPrice = purchaseCostPrice;
    }

    public Set<VariationOptions> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(Set<VariationOptions> variationOptions) {
        this.variationOptions = variationOptions;
    }

    public String getVariationOptionNames() {
        return variationOptionNames;
    }

    public void setVariationOptionNames(String variationOptionNames) {
        this.variationOptionNames = variationOptionNames;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseItems that = (PurchaseItems) o;
        return Objects.equals(purchaseItemsId, that.purchaseItemsId) && Objects.equals(purchase, that.purchase) && Objects.equals(product, that.product) && Objects.equals(purchaseItemQuantity, that.purchaseItemQuantity) && Objects.equals(purchaseCostPrice, that.purchaseCostPrice)&& Objects.equals(variationOptions, that.variationOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseItemsId, purchase, product, purchaseItemQuantity, purchaseCostPrice, variationOptions);
    }



}
