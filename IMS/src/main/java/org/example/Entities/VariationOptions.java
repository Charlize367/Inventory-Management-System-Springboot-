package org.example.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.*;

@Entity
public class VariationOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variationOptionId;

    @NotBlank(message = "Variation option cannot be blank")
    @Size(max = 50, message = "Option name must have at least 1-50 characters")
    private String variationOptionName;

    @NotBlank(message = "Variation option code cannot be blank")
    @Size(max = 5, message = "Code name must have at least 1-5 characters")
    private String variationOptionCode;


    @NotNull(message = "Price is required")
    @DecimalMax(value = "1000000.00", message = "Price must not exceed 1,000,000")
    private Double variationPriceAdjustment;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "variation_id")
    @NotNull(message = "Variation is required")
    private Variation variation;


    @ManyToMany(mappedBy = "variationOptions")
    private Set<Sku> skus = new HashSet<>();

    @ManyToMany(mappedBy = "variationOptions")
    private List<PurchaseItems> purchaseItems = new ArrayList<>();



    @ManyToMany(mappedBy = "variationOptions")
    private List<SaleItems> saleItems = new ArrayList<>();

    public String getVariationOptionName() {
        return variationOptionName;
    }

    public void setVariationOptionName(String variationOptionName) {
        this.variationOptionName = variationOptionName;
    }

    public Long getVariationOptionId() {
        return variationOptionId;
    }

    public void setVariationOptionId(Long variationOptionId) {
        this.variationOptionId = variationOptionId;
    }

    public Variation getVariation() {
        return variation;
    }

    public void setVariation(Variation variation) {
        this.variation = variation;
    }


    public String getVariationOptionCode() {
        return variationOptionCode;
    }


    public void setVariationOptionCode(String variationOptionCode) {
        this.variationOptionCode = variationOptionCode;
    }

    public Double getVariationPriceAdjustment() {
        return variationPriceAdjustment;
    }

    public void setVariationPriceAdjustment(Double variationPriceAdjustment) {
        this.variationPriceAdjustment = variationPriceAdjustment;
    }

    public Set<Sku> getSkus() {
        return skus;
    }

    public void setSkus(Set<Sku> skus) {
        this.skus = skus;
    }

    public List<SaleItems> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItems> saleItems) {
        this.saleItems = saleItems;
    }

    public List<PurchaseItems> getPurchaseItems() {
        return purchaseItems;
    }

    public void setPurchaseItems(List<PurchaseItems> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VariationOptions that = (VariationOptions) o;
        return Objects.equals(variationOptionId, that.variationOptionId) && Objects.equals(variationOptionName, that.variationOptionName) && Objects.equals(variationOptionCode, that.variationOptionCode) && Objects.equals(variationPriceAdjustment, that.variationPriceAdjustment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variationOptionId, variationOptionName, variationOptionCode, variationPriceAdjustment);
    }
}
