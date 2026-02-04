package org.example.Mapper;

import org.example.DTO.Response.*;
import org.example.Entities.Sku;
import org.example.Entities.Users;
import org.example.Entities.Variation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class SkuMapper {

    public SkuMapper(ProductMapper productMapper, VariationOptionsMapper variationOptionsMapper) {
        this.productMapper = productMapper;
        this.variationOptionsMapper = variationOptionsMapper;

    }

    private final ProductMapper productMapper;
    private final VariationOptionsMapper variationOptionsMapper;

    public SkuResponse toResponse(Sku sku) {

        SkuResponse response = new SkuResponse();
        response.setSkuId(sku.getSkuId());
        response.setSkuCode(sku.getSkuCode());
        response.setProduct(productMapper.toResponse(sku.getProduct()));
        response.setFinalPrice(sku.getFinalPrice());
        response.setStockQuantity(sku.getStockQuantity());
        response.setActive(sku.isActive());

        List<VariationOptionsResponse> variationResponses = sku.getVariationOptions()
                .stream()
                .map(variationOptionsMapper::toResponse)
                .toList();
        response.setVariationOptions(variationResponses);
        return response;
    }


}
