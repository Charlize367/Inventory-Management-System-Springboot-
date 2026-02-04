package org.example.Controllers;

import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Request.PurchaseRequest;
import org.example.DTO.Request.PurchaseStatusRequest;
import org.example.DTO.Response.CustomerResponse;
import org.example.DTO.Response.PurchaseResponse;
import org.example.Repository.PurchaseRepository;
import org.example.Services.PurchaseService;
import org.example.Entities.Purchases;
import org.example.Repository.ProductRepository;
import org.example.Entities.Products;
import org.example.Entities.PurchaseItems;
import org.example.Repository.PurchaseItemsRepository;
import org.example.Entities.StockMovements;
import org.example.Repository.StockMovementsRepository;
import org.example.Entities.Suppliers;
import org.example.Repository.SuppliersRepository;
import org.example.Entities.Users;
import org.example.Repository.UsersRepository;
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

import java.util.List;

@RestController
@RequestMapping("api/v1/purchases")
public class PurchaseController {

    @Autowired
    PurchaseRepository purchaseRepository;
    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseResponse>> getPurchases(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size,
                                                               @RequestParam(defaultValue = "purchaseId") String sortBy,
                                                               @RequestParam(defaultValue = "true") boolean descending,
                                                               @RequestParam(required = false) Long staffId ) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(purchaseService.getAllPurchases(staffId, pageable));
    }

    @Cacheable("purchases")
    @GetMapping("{id}")
    public ResponseEntity<PurchaseResponse> getPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }


    @PostMapping
    public ResponseEntity<PurchaseResponse> addPurchase(@Validated @RequestBody PurchaseRequest request) {
        purchaseService.addPurchase(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseResponse> updatePurchaseStatus(@Validated @PathVariable Long id, @RequestBody PurchaseStatusRequest request) {
        purchaseService.updatePurchaseStatus(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@Validated @PathVariable Long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.noContent().build();
    }


}
