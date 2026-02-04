package org.example.Controllers;


import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Request.SupplierRequest;
import org.example.DTO.Response.CustomerResponse;
import org.example.DTO.Response.SupplierResponse;
import org.example.Entities.Suppliers;
import org.example.Repository.SuppliersRepository;
import org.example.Services.SuppliersService;
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
@RequestMapping("api/v1/suppliers")
public class SuppliersController {

    @Autowired
    SuppliersRepository suppliersRepository;
    private final SuppliersService suppliersService;

    public SuppliersController(SuppliersService suppliersService) {
        this.suppliersService = suppliersService;
    }


    @GetMapping
    public ResponseEntity<Page<SupplierResponse>> getSuppliers(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size,
                                                               @RequestParam(defaultValue = "supplierId") String sortBy,
                                                               @RequestParam(defaultValue = "true") boolean descending) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(suppliersService.getSuppliers(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        return ResponseEntity.ok(suppliersService.getAllSuppliers());
    }


    @Cacheable("suppliers")
    @GetMapping("{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(suppliersService.getSupplierById(id));
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SupplierResponse> addSupplier(@Validated @RequestBody SupplierRequest request) {
        suppliersService.addSupplier(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> updateSupplier(@Validated @PathVariable Long id, @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(suppliersService.updateSupplierDetails(id, request));
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        suppliersService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }


}
