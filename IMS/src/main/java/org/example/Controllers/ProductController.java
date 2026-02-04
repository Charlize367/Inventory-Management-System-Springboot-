package org.example.Controllers;


import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Request.ProductRequest;
import org.example.DTO.Response.CustomerResponse;
import org.example.DTO.Response.ProductResponse;
import org.example.Entities.Categories;
import org.example.Repository.CategoryRepository;
import org.example.Entities.Products;
import org.example.Repository.ProductRepository;
import org.example.Services.ProductService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size,
                                                             @RequestParam(defaultValue = "productId") String sortBy,
                                                             @RequestParam(defaultValue = "true") boolean descending,
                                                            @RequestParam(required = false) Long categoryId,
                                                             @RequestParam(required = false) Long brandId) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);


        return ResponseEntity.ok(productService.getProducts(categoryId, brandId, pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam(required = false) Long categoryId, @RequestParam(required = false) Long brandId, @RequestParam(required = false) String query) {
        return ResponseEntity.ok(productService.getAllProducts(categoryId, brandId, query));
    }

    @Cacheable("products")
    @GetMapping("{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }


    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Validated @RequestBody ProductRequest request) {
        ProductResponse saved = productService.addProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @PostMapping("/image/{id}")
    public ResponseEntity<Void> uploadImage(
            @PathVariable Long id,
            @RequestParam("productImage") MultipartFile file
    ) throws IOException {
        productService.saveImage(id, file);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@Validated @PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProductDetails(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public long getTotalProducts() {
        return productRepository.count();
    }


}
