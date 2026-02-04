package org.example.Mapper;

import org.example.DTO.Response.SaleItemResponse;
import org.example.DTO.Response.VariationOptionsResponse;
import org.example.Entities.SaleItems;
import org.example.Entities.Variation;
import org.example.Entities.VariationOptions;
import org.example.Repository.VariationOptionsRepository;
import org.example.Repository.VariationRepository;
import org.springframework.stereotype.Component;

@Component
public class VariationOptionsMapper {

    public VariationOptionsMapper(VariationMapper variationMapper, VariationRepository variationRepository, VariationOptionsRepository variationOptionsRepository) {
        this.variationMapper = variationMapper;
        this.variationRepository = variationRepository;
        this.variationOptionsRepository = variationOptionsRepository;
    }

    private final VariationMapper variationMapper;
    private final VariationRepository variationRepository;
    private final VariationOptionsRepository variationOptionsRepository;

    public VariationOptionsResponse toResponse(VariationOptions variationOptions) {
        VariationOptionsResponse response = new VariationOptionsResponse();
        Variation var = variationRepository.findByVariationOptions(variationOptions);

        VariationOptions vo = variationOptionsRepository.findById(variationOptions.getVariationOptionId()).orElseThrow();
        Variation variation = vo.getVariation();

        response.setVariationOptionId(variationOptions.getVariationOptionId());
        response.setVariationId(var.getVariationId());
        response.setVariationOptionName(variationOptions.getVariationOptionName());
        response.setVariationOptionCode(variationOptions.getVariationOptionCode());
        response.setVariationPriceAdjustment(variationOptions.getVariationPriceAdjustment());

        return response;
    }
}
