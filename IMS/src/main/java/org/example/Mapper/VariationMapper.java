package org.example.Mapper;

import org.example.DTO.Response.VariationOptionsResponse;
import org.example.DTO.Response.VariationResponse;
import org.example.Entities.Variation;
import org.example.Repository.VariationOptionsRepository;
import org.example.Repository.VariationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VariationMapper {

    private final VariationOptionsRepository variationOptionsRepository;
    private final VariationRepository variationRepository;

    public VariationMapper(VariationOptionsRepository variationOptionsRepository, VariationRepository variationRepository) {
        this.variationOptionsRepository = variationOptionsRepository;
        this.variationRepository = variationRepository;
    }

    public VariationResponse toResponse(Variation variation) {

        List<VariationOptionsResponse> variationOptions = variationOptionsRepository.findAllByVariation(variation)
                .stream()
                .map(option -> {
                    VariationOptionsResponse dto = new VariationOptionsResponse();
                    dto.setVariationOptionId(option.getVariationOptionId());
                    dto.setVariationOptionName(option.getVariationOptionName());
                    dto.setVariationOptionCode(option.getVariationOptionCode());
                    dto.setVariationPriceAdjustment(option.getVariationPriceAdjustment());
                    return dto;
                })
                .toList();


        VariationResponse response = new VariationResponse();
        response.setVariationId(variation.getVariationId());
        response.setVariationName(variation.getVariationName());
        response.setVariationOptions(variationOptions);
        return response;

    }

    public Set<VariationResponse> toSetResponse(Set<Variation> variations) {
        return variations.stream()
                .map(variation -> {
        List<VariationOptionsResponse> variationOptions = variationOptionsRepository.findAllByVariation(variation)
                .stream()
                .map(option -> {
                    VariationOptionsResponse dto = new VariationOptionsResponse();
                    dto.setVariationOptionId(option.getVariationOptionId());
                    dto.setVariationOptionName(option.getVariationOptionName());
                    dto.setVariationOptionCode(option.getVariationOptionCode());
                    dto.setVariationPriceAdjustment(option.getVariationPriceAdjustment());
                    return dto;
                })
                .toList();


        VariationResponse response = new VariationResponse();
        response.setVariationId(variation.getVariationId());
        response.setVariationName(variation.getVariationName());
        response.setVariationOptions(variationOptions);
        return response;
                })
                .collect(Collectors.toSet());
    }
}
