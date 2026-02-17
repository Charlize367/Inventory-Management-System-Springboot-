package org.example.DTO.Request;


import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import org.example.Entities.Suppliers;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ProductRequest {
    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must have at least 1-50 characters")
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
    @DecimalMin(value = "0.01", message = "Cost must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Cost must not exceed 1,000,000")
    private Double productCost;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be at least 0")
    private Integer productStock;

    @NotNull(message = "Category ID is required")
    private List<Long> categoryIds;

    @NotNull(message = "User ID is required")
    private Long staffId;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotBlank
    private String productImage;

    private List<Long> variationIds;





    public ProductRequest(String productName, String productDescription, Double productPrice, Double productCost, Integer productStock, List<Long> categoryIds, String productCode, Long staffId, Long supplierId, Long brandId, List<Long> variationIds, String productImage) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productCost = productCost;
        this.productStock = productStock;
        this.categoryIds = categoryIds;
        this.productCode = productCode;
        this.supplierId = supplierId;
        this.brandId = brandId;
        this.staffId = staffId;
        this.variationIds = variationIds;
        this.productImage = productImage;
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

    public List<Long> getCategoryId() {
        return categoryIds;
    }

    public void setCategoryId(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Long> getVariationIds() {
        return variationIds;
    }

    public void setVariationIds(List<Long> variationIds) {
        this.variationIds = variationIds;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }


}
