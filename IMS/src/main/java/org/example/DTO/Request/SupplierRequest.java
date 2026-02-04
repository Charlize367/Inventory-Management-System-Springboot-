package org.example.DTO.Request;

import jakarta.validation.constraints.*;

public class SupplierRequest {
    @NotBlank(message = "Supplier name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must have at least 1-50 characters")
    private String supplierName;

    @NotBlank(message = "Contact name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must have at least 1-50 characters")
    private String supplierContactName;


    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String supplierEmail;

    @NotBlank(message = "Phone Number cannot be blank")
    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{10,15}$", message = "Invalid phone number format")
    private String supplierPhone;

    @NotBlank(message = "Address cannot be blank")
    private String supplierAddress;


    public SupplierRequest(String supplierName, String supplierContactName, String supplierEmail, String supplierPhone, String supplierAddress) {
        this.supplierName = supplierName;
        this.supplierContactName = supplierContactName;
        this.supplierEmail = supplierEmail;
        this.supplierPhone = supplierPhone;
        this.supplierAddress = supplierAddress;
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
}
