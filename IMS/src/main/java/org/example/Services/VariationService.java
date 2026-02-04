package org.example.Services;


import org.example.DTO.Request.*;
import org.example.DTO.Response.VariationResponse;
import org.example.Entities.*;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.VariationMapper;
import org.example.Repository.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VariationService {

    private static final Logger logger = LoggerFactory.getLogger(VariationService.class);

    private final VariationRepository variationRepository;
    private final VariationOptionsRepository variationOptionsRepository;
    private final VariationMapper variationMapper;
    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;

    public VariationService(VariationRepository variationRepository, VariationOptionsRepository variationOptionsRepository, VariationMapper variationMapper, ProductRepository productRepository, SkuRepository skuRepository) {
        this.variationRepository = variationRepository;
        this.variationOptionsRepository = variationOptionsRepository;
        this.variationMapper = variationMapper;
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
    }

    public Page<VariationResponse> getVariations(Pageable pageable) {
        logger.info("Displaying all customers");
        return variationRepository.findAll(pageable)
                .map(variationMapper::toResponse);
    }


    public List<VariationResponse> getAllVariations() {
        logger.info("Displaying all customers without pagination");
        return variationRepository.findAll().stream()
                .map(variationMapper::toResponse)
                .toList();
    }


    public VariationResponse createVariation(VariationRequest request) {
        logger.info("Attempting to create new variation with");



        String safeName = Jsoup.clean(request.getVariationName(), Safelist.none()).trim();

        Variation newVariation = new Variation();
        newVariation.setVariationName(safeName);

        Variation savedVar = variationRepository.save(newVariation);

        List<VariationOptions> optionList = new ArrayList<>();


        for (VariationOptionRequest option : request.getVariationOptions()) {

            VariationOptions variationOptions = new VariationOptions();
            variationOptions.setVariation(savedVar);
            variationOptions.setVariationOptionName(option.getVariationOptionName());
            variationOptions.setVariationOptionCode(option.getVariationOptionCode());
            variationOptions.setVariationPriceAdjustment(option.getVariationPriceAdjustment());
            optionList.add(variationOptions);



        }
        savedVar.setVariationOptions(optionList);
        Variation finalVar = variationRepository.save(savedVar);



        return variationMapper.toResponse(finalVar);
    }

    public List<VariationResponse> assignExistingVariation(AssignVariationRequest request, Long id) {
        logger.info("Attempting to assign new variation");

        List<Variation> variation = variationRepository.findAllById(request.getVariationIds()
                .stream().toList());



        Products product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setVariations(new HashSet<>(variation));

        productRepository.save(product);

        List<String> skus = generateSKUs(
                product.getProductCode(),
                new ArrayList<>(product.getVariations())
        );



        for (String sku : skus) {
            Sku skuData = new Sku();
            String[] parts = sku.split("-");
            List<String> optionCodes = Arrays.asList(parts).subList(1, parts.length);
            List<VariationOptions> optionsForSku = variationOptionsRepository.findAll()
                    .stream()
                    .filter(option -> optionCodes.contains(option.getVariationOptionCode()))
                    .toList();

            double totalAdjustment = optionsForSku.stream()
                    .mapToDouble(VariationOptions::getVariationPriceAdjustment)
                    .sum();


            Sku skuInfo = skuRepository.getBySkuCode(sku);
            skuData.setSkuCode(sku);
            if (skuInfo != null) {
                skuData.setStockQuantity(skuInfo.getStockQuantity());
            } else {
                skuData.setStockQuantity(0);
            }
            skuData.setFinalPrice(product.getProductPrice() + totalAdjustment);
            skuData.setActive(true);
            skuRepository.save(skuData);

        }

        return variation.stream()
                .map(variationMapper::toResponse)
                .toList();
    }

    public VariationResponse editVariationName(Long id, UpdateVariationNameRequest request) {

        logger.info("Updating status id: {} with new name: {}", id, request.getVariationName());

        Variation variation = variationRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Variation not found with id: {}", id);
                    return new ResourceNotFoundException("Variation not found");
                });

        variation.setVariationName(request.getVariationName());

        Variation updatedVarName = variationRepository.save(variation);
        logger.info("Successfully updated variation id: {} to name: {}", id, request.getVariationName());

        return variationMapper.toResponse(updatedVarName);
    }

    public void deleteVariation(Long id) {

        logger.info("Attempting to delete variation by ID");
        Variation variation = variationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variation not found"));


        variationRepository.delete(variation);

        logger.info("Successfully deleted variation: {} (id: {})", variation.getVariationName(), id);
    }



    public List<String> generateSKUs(String productCode, List<Variation> variations) {

        List<List<String>> variationPairs = new ArrayList<>();
        variationPairs.add(new ArrayList<>());

        for (Variation variation : variations) {
            List<List<String>> tempPairs = new ArrayList<>();

            for(List<String> pairsCreated : variationPairs) {
                for (VariationOptions option : variation.getVariationOptions()) {
                    List<String> newPair = new ArrayList<>(pairsCreated);
                    newPair.add(option.getVariationOptionCode());
                    tempPairs.add(newPair);
                }
            }

            variationPairs = tempPairs;
        }

        return variationPairs.stream()
                .map(combo -> productCode.toUpperCase( ) + "-" + String.join("-", combo))
                .collect(Collectors.toList());
    }

}
