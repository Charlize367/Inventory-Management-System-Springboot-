package org.example.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.*;

@Entity
public class SaleItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleItemsId;

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name = "salesId")
    @NotNull(message = "Sales is required")
    private Sales sales;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "productId")
    @NotNull(message = "Product is required")
    private Products product;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer salesItemQuantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Price must not exceed 1,000,000")
    private Double salesPrice;



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "sale_item_variation_options",
            joinColumns = @JoinColumn(name = "variationOptionId"),
            inverseJoinColumns = @JoinColumn(name = "saleItemsId")
    )
    private Set<VariationOptions> variationOptions = new HashSet<>();




    public SaleItems(Long saleItemsId, Sales sales, Products product, Integer salesItemQuantity, Double salesPrice) {
        this.saleItemsId = saleItemsId;
        this.sales = sales;
        this.product = product;
        this.salesItemQuantity = salesItemQuantity;
        this.salesPrice = salesPrice;
    }

    public SaleItems(){}

    public Long getSaleItemsId() {
        return saleItemsId;
    }

    public void setSaleItemsId(Long saleItemsId) {
        this.saleItemsId = saleItemsId;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public Integer getSalesItemQuantity() {
        return salesItemQuantity;
    }

    public void setSalesItemQuantity(Integer salesItemQuantity) {
        this.salesItemQuantity = salesItemQuantity;
    }

    public Double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(Double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public Set<VariationOptions> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(Set<VariationOptions> variationOptions) {
        this.variationOptions = variationOptions;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SaleItems saleItems = (SaleItems) o;
        return Objects.equals(saleItemsId, saleItems.saleItemsId) && Objects.equals(sales, saleItems.sales) && Objects.equals(product, saleItems.product) && Objects.equals(salesItemQuantity, saleItems.salesItemQuantity) && Objects.equals(salesPrice, saleItems.salesPrice)&& Objects.equals(variationOptions, saleItems.variationOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleItemsId, sales, product, salesItemQuantity, salesPrice, variationOptions);
    }
}
