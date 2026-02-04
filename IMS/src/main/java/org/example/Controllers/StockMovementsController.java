package org.example.Controllers;

import org.example.DTO.Request.CustomerRequest;
import org.example.DTO.Request.StockMovementRequest;
import org.example.DTO.Response.CategoryResponse;
import org.example.DTO.Response.CustomerResponse;
import org.example.DTO.Response.StockMovementResponse;
import org.example.Repository.CategoryRepository;
import org.example.Repository.StockMovementsRepository;
import org.example.Services.CategoryService;
import org.example.Services.StockMovementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stockMovements")
public class StockMovementsController {

    @Autowired
    StockMovementsRepository stockMovementsRepository;
    private final StockMovementsService stockMovementsService;


    public StockMovementsController(StockMovementsService stockMovementsService) {
        this.stockMovementsService = stockMovementsService;
    }

    @GetMapping
    public ResponseEntity<Page<StockMovementResponse>> getStockMovements(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "5") int size,
                                                                         @RequestParam(defaultValue = "stockMovementId") String sortBy,
                                                                         @RequestParam(defaultValue = "true") boolean descending) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);


        return ResponseEntity.ok(stockMovementsService.getAllStockMovements(pageable));
    }


    @Cacheable("stockMovements")
    @GetMapping("{id}")
    public ResponseEntity<StockMovementResponse> getStockMovementsById(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovementsService.getStockMovementById(id));
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StockMovementResponse> updateStockMovement(@Validated @PathVariable Long id, @RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(stockMovementsService.updateStockMovement(id, request));
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {
        stockMovementsService.deleteStockMovement(id);
        return ResponseEntity.noContent().build();
    }
}
