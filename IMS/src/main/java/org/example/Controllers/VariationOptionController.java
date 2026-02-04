package org.example.Controllers;


import org.example.DTO.Request.UpdateVarOptionRequest;
import org.example.DTO.Request.VariationOptionRequest;
import org.example.DTO.Response.VariationOptionsResponse;
import org.example.Repository.VariationOptionsRepository;
import org.example.Services.VariationOptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/variationOptions")
public class VariationOptionController {

    @Autowired
    VariationOptionsRepository variationOptionsRepository;


    private final VariationOptionsService variationOptionsService;

    public VariationOptionController(VariationOptionsService variationOptionsService) {
        this.variationOptionsService = variationOptionsService;
    }

    @PostMapping("/variation/{variationId}")
    public ResponseEntity<VariationOptionsResponse> addOptionToVariation(@Validated @RequestBody VariationOptionRequest request, @PathVariable Long variationId) {
        variationOptionsService.addVariationOptionToVariation(request, variationId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VariationOptionsResponse> editVariationOptionsName(@Validated @PathVariable Long id, @RequestBody UpdateVarOptionRequest request) {
        variationOptionsService.editVariationOption(id, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariationOption(@PathVariable Long id) {
        variationOptionsService.deleteVariationOption(id);
        return ResponseEntity.noContent().build();
    }
}
