package org.example.Services;


import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Response.BrandResponse;
import org.example.DTO.Response.CustomerResponse;
import org.example.DTO.Response.PageResponse;
import org.example.Entities.Brand;
import org.example.Entities.Customers;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.CustomerMapper;
import org.example.Repository.CustomersRepository;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomersService {

    private static final Logger logger = LoggerFactory.getLogger(CustomersService.class);

    private final CustomersRepository customersRepository;
    private final CustomerMapper customerMapper;

    public CustomersService(CustomersRepository customersRepository, CustomerMapper customerMapper) {
        this.customersRepository = customersRepository;
        this.customerMapper = customerMapper;

    }

    @Cacheable(value = "customers", key = "'page_'+#pageable.pageNumber+'_'+#pageable.pageSize+'_'+#pageable.sort.toString()")
    public PageResponse<CustomerResponse> getCustomers(Pageable pageable) {
        logger.info("Displaying all customers");
        Page<Customers> page = customersRepository.findAll(pageable);

        Page<CustomerResponse> mapped = page.map(customerMapper::toResponse);

        return new PageResponse<>(mapped);
    }

    @NotNull
    private Page<Customers> getCustomersPage(Pageable pageable) {
        Page<Customers> page = customersRepository.findAll(pageable);
        return page;
    }


    public List<CustomerResponse> getAllCustomers() {
        logger.info("Displaying all customers without pagination");
        return customersRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Cacheable(value = "customer", key = "#userId")
    public CustomerResponse getCustomerById(Long userId) {
        System.out.println("Fetching customer from DB: " + userId);
        logger.info("Fetching customer with id: {}", userId);
        Customers customer = customersRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Customer with id: {} does not exist", userId);
                    return new ResourceNotFoundException("Customer not found.");
                });
        logger.info("Successfully fetched customer:{} (id: {})", customer.getCustomerName(), customer.getCustomerId() );
        return customerMapper.toResponse(customer);
    }



    @Caching(
            put = {
                    @CachePut(value = "customer", key = "#result.userId")
            },
            evict = {
                    @CacheEvict(value = "customers", allEntries = true)
            }
    )
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

    @Caching(
            put = {
                    @CachePut(value = "customer", key = "#result.userId")
            },
            evict = {
                    @CacheEvict(value = "customers", allEntries = true)
            }
    )
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

    @Caching(evict = {
            @CacheEvict(value = "customer", key = "#userId"),
            @CacheEvict(value = "customers", allEntries = true)
    })
    public void deleteCustomer(Long userId) {

        logger.info("Attempting to delete customer by ID");

        Customers customers = customersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customersRepository.delete(customers);

        logger.info("Successfully deleted customer: {} (userId: {})", customers.getCustomerName(), userId);
    }
}
