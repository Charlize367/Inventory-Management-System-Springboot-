package org.example.Controllers;

import org.example.DTO.Request.CategoryRequest;
import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Response.CategoryResponse;
import org.example.DTO.Response.CustomerResponse;
import org.example.Entities.Customers;
import org.example.Repository.CustomersRepository;
import org.example.Services.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomersController {

    @Autowired
    CustomersRepository customersRepository;
    private final CustomersService customersService;


    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }


    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getCustomers(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size,
                                                               @RequestParam(defaultValue = "customerId") String sortBy,
                                                               @RequestParam(defaultValue = "true") boolean descending) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(customersService.getCustomers(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customersService.getAllCustomers());
    }


    @Cacheable("customers")
    @GetMapping("{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customersService.getCustomerById(id));
    }


    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CustomerResponse> addCustomer(@Validated @RequestBody CustomerRequest request) {
        customersService.addCustomer(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@Validated @PathVariable Long id, @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customersService.updateCustomerDetails(id, request));
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customersService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

}
