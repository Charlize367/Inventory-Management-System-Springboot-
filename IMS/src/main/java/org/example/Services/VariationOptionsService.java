package org.example.Services;


import org.example.DTO.Request.UpdateVarOptionRequest;
import org.example.DTO.Request.VariationOptionRequest;
import org.example.DTO.Response.VariationOptionsResponse;
import org.example.Entities.*;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.VariationOptionsMapper;
import org.example.Repository.ProductRepository;
import org.example.Repository.SkuRepository;
import org.example.Repository.VariationOptionsRepository;
import org.example.Repository.VariationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Service
public class VariationOptionsService {

    private final VariationOptionsRepository variationOptionsRepository;
    private final VariationOptionsMapper variationOptionsMapper;
    private final VariationRepository variationRepository;
    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;
    private final VariationService variationService;
    private static final Logger logger = LoggerFactory.getLogger(VariationOptionsService.class);

    public VariationOptionsService(VariationOptionsRepository variationOptionsRepository, VariationOptionsMapper variationOptionsMapper, VariationRepository variationRepository, ProductRepository productRepository, SkuRepository skuRepository, VariationService variationService) {
        this.variationOptionsRepository = variationOptionsRepository;
        this.variationOptionsMapper = variationOptionsMapper;
        this.variationRepository = variationRepository;
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
        this.variationService = variationService;
    }

    @PostMapping
    public VariationOptionsResponse addVariationOptionToVariation(VariationOptionRequest request, Long variationId) {

        logger.info("Attempting to add new category with name: {}", request.getVariationOptionName());

        Variation variation = variationRepository.findById(variationId)
                .orElseThrow(() -> {
                    logger.error("Variation not found with id: {}", variationId);
                    return new ResourceNotFoundException("Variation not found");
                });


        boolean exists = variation.getVariationOptions().stream()
                .anyMatch(o -> o.getVariationOptionName().equalsIgnoreCase(request.getVariationOptionName()));
        if (exists) {
            throw new ResourceAlreadyExistsException("Variation Option already exists.");
        }

        VariationOptions newVarOptions = new VariationOptions();
        newVarOptions.setVariation(variation);
        newVarOptions.setVariationOptionName(request.getVariationOptionName());
        newVarOptions.setVariationOptionCode(request.getVariationOptionCode());
        newVarOptions.setVariationPriceAdjustment(request.getVariationPriceAdjustment());

        VariationOptions savedVarOptions = variationOptionsRepository.save(newVarOptions);

        List<VariationOptions> newVarOption = new ArrayList<>();
        newVarOption.add(savedVarOptions);


        List<Products> products = productRepository.findByVariations(variation);

        for(Products product : products) {
           String skuCode = product.getProductCode().toUpperCase( ) + "-" + request.getVariationOptionCode();

           Sku sku = new Sku();
           sku.setSkuCode(skuCode);
           sku.setFinalPrice(product.getProductPrice() + request.getVariationPriceAdjustment());
           sku.setStockQuantity(0);
           Products managedProduct = productRepository.findById(product.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            sku.setProduct(managedProduct);

           skuRepository.save(sku);
        }


            logger.info("Successfully added saved new option: {}", savedVarOptions.getVariationOptionName());

            return variationOptionsMapper.toResponse(savedVarOptions);
        }

    public VariationOptionsResponse editVariationOption(Long id, UpdateVarOptionRequest request) {

        logger.info("Updating status id: {} with new name: {}", id, request.getVariationOptionName());

        VariationOptions varOption = variationOptionsRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Variation option not found with id: {}", id);
                    return new ResourceNotFoundException("Variation option not found");
                });

        Double oldAdjustment = varOption.getVariationPriceAdjustment();

        varOption.setVariationOptionName(request.getVariationOptionName());
        varOption.setVariationPriceAdjustment(request.getVariationPriceAdjustment());
        VariationOptions updatedVarOptionName = variationOptionsRepository.save(varOption);




        if(!Objects.equals(oldAdjustment, request.getVariationPriceAdjustment())) {


            List<Sku> skus = skuRepository.findByVariationOptions_VariationOptionId(id);


            for (Sku sku : skus) {
                Products product = sku.getProduct();
                if(product != null) {

                    double totalAdjustment = sku.getVariationOptions().stream()
                            .mapToDouble(VariationOptions::getVariationPriceAdjustment)
                            .sum();
                    sku.setFinalPrice(product.getProductPrice() + totalAdjustment);

                }
            }

            skuRepository.saveAll(skus);
        }

        logger.info("Successfully updated variation id: {} to name: {}", id, request.getVariationOptionName());


        return variationOptionsMapper.toResponse(updatedVarOptionName);
    }

    public void deleteVariationOption(Long variationOptionId) {

        logger.info("Attempting to delete variation option by ID");

        VariationOptions option = variationOptionsRepository.findById(variationOptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Variation option not found"));



        variationOptionsRepository.delete(option);

        logger.info("Successfully deleted variation option: {} (id: {})", option.getVariationOptionName(), variationOptionId);
    }


    }



