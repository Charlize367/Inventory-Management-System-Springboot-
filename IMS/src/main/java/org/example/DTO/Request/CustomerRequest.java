package org.example.DTO.Request;

import jakarta.validation.constraints.*;

public class CustomerRequest {

    @NotBlank(message = "Customer name cannot be blank")
    @Size(min = 1, max = 50, message = "Name must have at least 1-50 characters")
    private String customerName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String customerEmail;

    @NotNull(message = "Phone Number is required")
    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{10,15}$", message = "Invalid phone number format")
    private String customerPhone;

    @NotBlank(message = "Address cannot be blank")
    private String customerAddress;

    @NotBlank(message = "Customer type is required")
    private String customerType;

    public CustomerRequest(String customerName, String customerEmail, String customerPhone, String customerAddress, String customerType) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.customerType = customerType;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
}
