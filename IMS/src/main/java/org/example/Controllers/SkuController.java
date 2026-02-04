package org.example.Controllers;

import org.example.DTO.Request.*;
import org.example.DTO.Response.PurchaseResponse;
import org.example.DTO.Response.SkuResponse;
import org.example.DTO.Response.UserResponse;
import org.example.Entities.Sku;
import org.example.Exception.ResourceNotFoundException;
import org.example.Repository.PurchaseRepository;
import org.example.Repository.SkuRepository;
import org.example.Services.PurchaseService;
import org.example.Services.SkuService;
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
@RequestMapping("api/v1/sku")
public class SkuController {


    private final SkuService skuService;
    private final SkuRepository skuRepository;

    public SkuController(SkuService skuService, SkuRepository skuRepository) {
        this.skuService = skuService;
        this.skuRepository = skuRepository;

    }

    @GetMapping
    public ResponseEntity<Page<SkuResponse>> getSku(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size,
                                                          @RequestParam(defaultValue = "skuId") String sortBy,
                                                          @RequestParam(defaultValue = "true") boolean descending
                                                          ) {
        Sort sort = descending ?  Sort.by(Sort.Direction.DESC, "stockQuantity") : Sort.by(sortBy).descending() ;
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(skuService.getSkus(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<SkuResponse>> getAllSku() {
        return ResponseEntity.ok(skuService.getAllSkus());

    }

    @GetMapping("/skuCode/{skuCode}")
    public ResponseEntity<SkuResponse> getSkuByCode(@PathVariable String skuCode) {
        System.out.println(skuCode);
        return ResponseEntity.ok(skuService.findSkuByCode(skuCode));

    }

    @PutMapping("/{id}")
    public ResponseEntity<SkuResponse> updateSkuActiveStatus(@Validated @PathVariable Long id, @RequestBody SkuRequest request) {
        skuService.updateSKUStatus(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/toggleActive/{id}")
    public ResponseEntity<Void> toggleSkuActive(@PathVariable Long id) {
        Sku sku = skuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SKU not found"));

        sku.setActive(!sku.isActive());
        skuRepository.save(sku);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/editCode/{id}")
    public ResponseEntity<SkuResponse> editCode(@PathVariable Long id, @RequestBody EditSkuCodeRequest request) {
        Sku sku = skuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SKU not found"));

        sku.setSkuCode(request.getSkuCode());
        skuRepository.save(sku);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSKU(@Validated @PathVariable Long id) {
        skuService.deleteSKU(id);
        return ResponseEntity.noContent().build();
    }
}
