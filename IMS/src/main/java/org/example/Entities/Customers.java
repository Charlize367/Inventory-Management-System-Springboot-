package org.example.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Customers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotBlank(message = "Category name cannot be blank")
    @Size( max = 50, message = "Name must have at least 1-50 characters")
    private String customerName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String customerEmail;

    @NotNull(message = "Phone Number is required")
    @Pattern(regexp = "^(\\+\\d{1,3})?\\d{10,15}$", message = "Invalid phone number format")
    private String customerPhone;

    @NotBlank(message = "Address cannot be blank")
    private String customerAddress;

    @NotNull(message = "Customer type is required")
    private CustomerType customerType;

    public enum CustomerType {
        RETAIL,
        WHOLESALE
    }


    public Customers(){}

    @OneToMany(mappedBy = "customer", fetch=FetchType.EAGER)
    private List<Sales> sales;



    public Customers(Long customerId, String customerName, String customerEmail, String customerPhone, String customerAddress, CustomerType customerType) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.customerType = customerType;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customers customers = (Customers) o;
        return Objects.equals(customerId, customers.customerId) && Objects.equals(customerName, customers.customerName) && Objects.equals(customerEmail, customers.customerEmail) && Objects.equals(customerPhone, customers.customerPhone) && Objects.equals(customerAddress, customers.customerAddress) && customerType == customers.customerType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerName, customerEmail, customerPhone, customerAddress, customerType);
    }




}
