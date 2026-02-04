package org.example.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Suppliers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    @NotBlank(message = "Supplier name cannot be blank")
    @Size(min = 2, message = "Name must have at least 2 characters")
    private String supplierName;

    @NotBlank(message = "Supplier name cannot be blank")
    @Size(min = 2, message = "Name must have at least 2 characters")
    private String supplierContactName;

    @Email(message = "Email should be valid")
    private String supplierEmail;

    @NotBlank(message = "Phone Number cannot be blank")
    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{10,15}$", message = "Invalid phone number format")
    private String supplierPhone;

    @NotBlank(message = "Address cannot be blank")
    private String supplierAddress;


    @OneToMany(mappedBy = "supplier", fetch=FetchType.EAGER)
    private List<Products> products;


    public Suppliers(Long supplierId, String supplierName, String supplierContactName, String supplierEmail, String supplierPhone, String supplierAddress) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierContactName = supplierContactName;
        this.supplierEmail = supplierEmail;
        this.supplierPhone = supplierPhone;
        this.supplierAddress = supplierAddress;
    }

    public Suppliers(){}



    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierContactName() {
        return supplierContactName;
    }

    public void setSupplierContactName(String supplierContactName) {
        this.supplierContactName = supplierContactName;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Suppliers suppliers = (Suppliers) o;
        return Objects.equals(supplierId, suppliers.supplierId) && Objects.equals(supplierName, suppliers.supplierName) && Objects.equals(supplierContactName, suppliers.supplierContactName) && Objects.equals(supplierEmail, suppliers.supplierEmail) && Objects.equals(supplierPhone, suppliers.supplierPhone) && Objects.equals(supplierAddress, suppliers.supplierAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId, supplierName, supplierContactName, supplierEmail, supplierPhone, supplierAddress);
    }





}
