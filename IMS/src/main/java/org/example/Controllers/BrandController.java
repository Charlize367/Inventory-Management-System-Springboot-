package org.example.Controllers;


import org.example.DTO.Request.BrandRequest;
import org.example.DTO.Request.CategoryRequest;
import org.example.DTO.Response.BrandResponse;
import org.example.DTO.Response.CategoryResponse;
import org.example.Repository.BrandRepository;
import org.example.Repository.CategoryRepository;
import org.example.Services.BrandService;
import org.example.Services.CategoryService;
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
@RequestMapping("/api/v1/brands")
public class BrandController {

    @Autowired
    BrandRepository brandRepository;
    private final BrandService brandService;


    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<Page<BrandResponse>> getCategories(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size,
                                                             @RequestParam(defaultValue = "brandId") String sortBy,
                                                             @RequestParam(defaultValue = "true") boolean descending) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);


        return ResponseEntity.ok(brandService.getBrands(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }


    @Cacheable("brands")
    @GetMapping("{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }


    @PostMapping
    public ResponseEntity<BrandResponse> addBrands(@Validated @RequestBody BrandRequest request) {
        brandService.addBrand(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrandName(@Validated @PathVariable Long id, @RequestBody BrandRequest request) {
        return ResponseEntity.ok(brandService.updateBrandName(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
