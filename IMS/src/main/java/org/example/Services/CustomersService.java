package org.example.Services;


import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Response.CustomerResponse;
import org.example.Entities.Categories;
import org.example.Entities.Customers;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.CustomerMapper;
import org.example.Repository.CustomersRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomersService {

    private static final Logger logger = LoggerFactory.getLogger(CustomersService.class);

    private final CustomersRepository customersRepository;
    private final CustomerMapper customerMapper;

    public CustomersService(CustomersRepository customersRepository, CustomerMapper customerMapper) {
        this.customersRepository = customersRepository;
        this.customerMapper = customerMapper;

    }

    public Page<CustomerResponse> getCustomers(Pageable pageable) {
        logger.info("Displaying all customers");
        return customersRepository.findAll(pageable)
                .map(customerMapper::toResponse);
    }


    public List<CustomerResponse> getAllCustomers() {
        logger.info("Displaying all customers without pagination");
        return customersRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        logger.info("Fetching customer with id: {}", id);
        Customers customer = customersRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer with id: {} does not exist", id);
                    return new ResourceNotFoundException("Customer not found.");
                });
        logger.info("Successfully fetched customer:{} (id: {})", customer.getCustomerName(), customer.getCustomerId() );
        return customerMapper.toResponse(customer);
    }

    public CustomerResponse addCustomer(CustomerRequest request) {

        logger.info("Attempting to add new customer with email: {}", request.getCustomerEmail());

        Customers customer = customersRepository.findByCustomerEmail(request.getCustomerEmail());

        if (customer != null) {
            logger.warn("Customer already exists with email: {}", request.getCustomerEmail());
            throw new ResourceAlreadyExistsException("Customer already exists.");
        }

        String safeName = Jsoup.clean(request.getCustomerName(), Safelist.none()).trim();
        String safeEmail = Jsoup.clean(request.getCustomerEmail(), Safelist.none()).trim();
        String safeAddress = Jsoup.clean(request.getCustomerAddress(), Safelist.none()).trim();
        String safePhoneNumber = Jsoup.clean(request.getCustomerPhone(), Safelist.none()).trim();

        Customers newCustomer = new Customers();
        newCustomer.setCustomerName(safeName);
        newCustomer.setCustomerEmail(safeEmail);
        newCustomer.setCustomerAddress(safeAddress);
        newCustomer.setCustomerPhone(safePhoneNumber);
        newCustomer.setCustomerType(Customers.CustomerType.valueOf(request.getCustomerType()));

        Customers savedCustomer = customersRepository.save(newCustomer);

        logger.info("Successfully added new customer: {} ({})", safeName, safeEmail);
        return customerMapper.toResponse(savedCustomer);
    }


    public CustomerResponse updateCustomerDetails(Long id, CustomerRequest request) {

        logger.info("Updating customer id: {} ", id);

        Customers customer = customersRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer not found with id: {}", id);
                    return new ResourceNotFoundException("Customer not found");
                });

        String safeName = Jsoup.clean(request.getCustomerName(), Safelist.none()).trim();
        String safeEmail = Jsoup.clean(request.getCustomerEmail(), Safelist.none()).trim();
        String safeAddress = Jsoup.clean(request.getCustomerAddress(), Safelist.none()).trim();
        String safePhoneNumber = Jsoup.clean(request.getCustomerPhone(), Safelist.none()).trim();

        customer.setCustomerName(safeName);
        customer.setCustomerEmail(safeEmail);
        customer.setCustomerAddress(safeAddress);
        customer.setCustomerPhone(safePhoneNumber);
        customer.setCustomerType(Customers.CustomerType.valueOf(request.getCustomerType()));

        Customers updatedCustomer = customersRepository.save(customer);

        logger.info("Successfully updated customer id: {}", id);

        return customerMapper.toResponse(updatedCustomer);
    }

    public void deleteCustomer(Long id) {

        logger.info("Attempting to delete customer by ID");

        Customers customers = customersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customersRepository.delete(customers);

        logger.info("Successfully deleted customer: {} (id: {})", customers.getCustomerName(), id);
    }
}
