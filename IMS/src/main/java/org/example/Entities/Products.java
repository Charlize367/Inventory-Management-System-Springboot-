package org.example.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Entity
public class Products {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 50, message = "Name must have at least 1-50 characters")
    private String productName;


    @NotBlank(message = "Product code cannot be blank")
    @Size(max = 10, message = "Name must have at least 1-10 characters")
    private String productCode;

    @NotBlank(message = "Product description cannot be blank")
    @Size(max = 100, message = "Name must have at least 1-100 characters")
    private String productDescription;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Price must not exceed 1,000,000")
    private Double productPrice;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Price must not exceed 1,000,000")
    private Double productCost;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be at least 0")
    private Integer productStock;



    private String productImage;

    @ManyToMany
    @JoinTable(name = "product_variations",
            joinColumns = @JoinColumn(name = "variationId"),
            inverseJoinColumns = @JoinColumn(name = "productId"))
    private Set<Variation> variations = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name = "brandId")
    @NotNull(message = "Brand is required")
    private Brand brand;

    @ManyToMany
    @JoinTable(name = "product_categories",
            joinColumns = @JoinColumn(name = "categoryId"),
            inverseJoinColumns = @JoinColumn(name = "productId"))
    private Set<Categories> categories = new HashSet<>();

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "supplierId")
    @NotNull(message = "Supplier is required")
    private Suppliers supplier;

    private boolean active = true;

    @OneToMany(mappedBy = "product", fetch=FetchType.EAGER)
    private List<SaleItems> saleItems;

    @OneToMany(mappedBy = "product", fetch=FetchType.EAGER)
    private List<Sku> sku;

    @OneToMany(mappedBy = "product", fetch=FetchType.EAGER)
    private List<PurchaseItems> purchaseItems;

    @OneToMany(mappedBy = "product", fetch=FetchType.EAGER)
    private List<StockMovements> stockMovements;



    public Products(Long productId, String productName, String productDescription, Double productPrice, Double productCost, Integer productStock, String productImage, Set<Categories> categories, Set<Variation> variations, boolean active, String productCode, Suppliers supplier, Brand brand) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productCost = productCost;
        this.productStock = productStock;
        this.productImage = productImage;
        this.categories = categories;
        this.variations = variations;
        this.active = active;
        this.productCode = productCode;
        this.supplier = supplier;
        this.brand = brand;
    }

    public Products() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Double getProductCost() {
        return productCost;
    }

    public void setProductCost(Double productCost) {
        this.productCost = productCost;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public boolean isActive() {
        return active;
    }

    public Suppliers getSupplier() {
        return supplier;
    }

    public void setSupplier(Suppliers supplier) {
        this.supplier = supplier;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Variation> getVariations() {
        return variations;
    }

    public void setVariations(Set<Variation> variations) {
        this.variations = variations;
    }

    public Set<Categories> getCategories() {
        return categories;
    }

    public void setCategories(Set<Categories> categories) {
        this.categories = categories;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Products products = (Products) o;
        return Objects.equals(productId, products.productId) && Objects.equals(productName, products.productName) && Objects.equals(productDescription, products.productDescription) && Objects.equals(productPrice, products.productPrice) && Objects.equals(productCost, products.productCost) && Objects.equals(productStock, products.productStock) && Objects.equals(productImage, products.productImage) && Objects.equals(categories, products.categories) && Objects.equals(variations, products.variations) && Objects.equals(productCode, products.productCode)  && Objects.equals(supplier, products.supplier) && Objects.equals(brand, products.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, productDescription, productPrice, productCost, productStock, productImage,  categories, variations, productCode, supplier, brand);
    }

    public List<Sku> getSku() {
        return sku;
    }

    public void setSku(List<Sku> sku) {
        this.sku = sku;
    }





}
