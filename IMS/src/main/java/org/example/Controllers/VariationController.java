package org.example.Controllers;

import org.example.DTO.Request.*;
import org.example.DTO.Response.CustomerResponse;
import org.example.DTO.Response.ProductResponse;
import org.example.DTO.Response.PurchaseResponse;
import org.example.DTO.Response.VariationResponse;
import org.example.Repository.ProductRepository;
import org.example.Repository.VariationRepository;
import org.example.Services.VariationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/variations")
public class VariationController {

    @Autowired
    VariationRepository variationRepository;


    private final VariationService variationService;

    public VariationController(VariationService variationService) {
        this.variationService = variationService;
    }

    @GetMapping
    public ResponseEntity<Page<VariationResponse>> getVariation(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size,
                                                               @RequestParam(defaultValue = "variationId") String sortBy,
                                                               @RequestParam(defaultValue = "true") boolean descending
                                                               ) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(variationService.getVariations(pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<List<VariationResponse>> getAllVariations() {
        return ResponseEntity.ok(variationService.getAllVariations());
    }


    @PostMapping
    public ResponseEntity<VariationResponse> createVariation(@Validated @RequestBody VariationRequest request) {
        variationService.createVariation(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VariationResponse> editVariationName(@Validated @PathVariable Long id, @RequestBody UpdateVariationNameRequest request) {
        variationService.editVariationName(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariation(@PathVariable Long id) {
        variationService.deleteVariation(id);
        return ResponseEntity.noContent().build();
    }



}
