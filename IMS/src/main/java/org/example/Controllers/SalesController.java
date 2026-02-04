package org.example.Controllers;


import org.example.DTO.Request.PurchaseRequest;
import org.example.DTO.Request.PurchaseStatusRequest;
import org.example.DTO.Request.SalesPaymentStatusRequest;
import org.example.DTO.Request.SalesRequest;
import org.example.DTO.Response.PurchaseResponse;
import org.example.DTO.Response.SalesResponse;
import org.example.Entities.Sales;
import org.example.Repository.SalesRepository;
import org.example.Services.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/sales")
public class SalesController {
    @Autowired
    SalesRepository salesRepository;
    private final SalesService salesService;


    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    public ResponseEntity<Page<SalesResponse>> getSales(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size,
                                                        @RequestParam(defaultValue = "salesId") String sortBy,
                                                        @RequestParam(defaultValue = "true") boolean descending,
                                                        @RequestParam(required = false) Long staffId) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(salesService.getAllSales(staffId, pageable));
    }

    @Cacheable("sales")
    @GetMapping("{id}")
    public ResponseEntity<SalesResponse> getSalesById(@PathVariable Long id) {
        return ResponseEntity.ok(salesService.getSalesById(id));
    }


    @PostMapping
    public ResponseEntity<SalesResponse> addSale(@Validated @RequestBody SalesRequest request) {
        salesService.addSale(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesResponse> updateSalesPaymentStatus(@Validated @PathVariable Long id, @RequestBody SalesPaymentStatusRequest request) {
        salesService.updateSalesPaymentStatus(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        salesService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }



}
