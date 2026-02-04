package org.example.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.aspectj.weaver.ast.Var;

import java.util.*;

@Entity
public class Variation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variationId;

    @NotBlank(message = "Variation name cannot be blank")
    @Size(max = 50, message = "Name must have at least 1-50 characters")
    private String variationName;



    @OneToMany(mappedBy = "variation",  cascade = CascadeType.ALL)
    private List<VariationOptions> variationOptions = new ArrayList<>();


    @ManyToMany(mappedBy = "variations")
    private Set<Products> products = new HashSet<>();

    public Variation(Long variationId, String variationName) {
        this.variationId = variationId;
        this.variationName = variationName;
    }


    public String getVariationName() {
        return variationName;
    }

    public void setVariationId(Long variationId) {
        this.variationId = variationId;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public Long getVariationId() {
        return variationId;
    }

    public List<VariationOptions> getVariationOptions() {
        return variationOptions;
    }

    public void setVariationOptions(List<VariationOptions> variationOptions) {
        this.variationOptions = variationOptions;
    }

    public Set<Products> getProducts() {
        return products;
    }

    public void setProducts(Set<Products> products) {
        this.products = products;
    }

    public Variation(){}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Variation variation = (Variation) o;
        return Objects.equals(variationId, variation.variationId) && Objects.equals(variationName, variation.variationName) && Objects.equals(variationOptions, variation.variationOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variationId, variationName, variationOptions);
    }
}
