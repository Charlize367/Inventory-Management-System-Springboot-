package org.example.Mapper;

import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Response.CustomerResponse;
import org.example.Entities.Customers;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public Customers toEntity(CustomerRequest req) {
        Customers customer = new Customers();
        customer.setCustomerName(req.getCustomerName());
        customer.setCustomerEmail(req.getCustomerEmail());
        customer.setCustomerAddress(req.getCustomerAddress());
        customer.setCustomerType(Customers.CustomerType.valueOf(req.getCustomerType()));
        customer.setCustomerPhone(req.getCustomerPhone());

        return customer;
    }

    public CustomerResponse toResponse(Customers customers) {
        if (customers == null) return null;
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setCustomerId(customers.getCustomerId());
        customerResponse.setCustomerName(customers.getCustomerName());
        customerResponse.setCustomerEmail(customers.getCustomerEmail());
        customerResponse.setCustomerAddress(customers.getCustomerAddress());
        customerResponse.setCustomerType(customers.getCustomerType().name());
        customerResponse.setCustomerPhone(customers.getCustomerPhone());
        return customerResponse;
    }
}
