package org.example.Mapper;

import org.example.DTO.Response.SaleItemResponse;
import org.example.DTO.Response.VariationOptionsResponse;
import org.example.Entities.SaleItems;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SaleItemsMapper {
    private final ProductMapper productMapper;
    private final VariationOptionsMapper variationOptionsMapper;


    public SaleItemsMapper(ProductMapper productMapper, VariationOptionsMapper variationOptionsMapper) {
        this.productMapper = productMapper;
        this.variationOptionsMapper = variationOptionsMapper;
    }


    public SaleItemResponse toResponse(SaleItems saleItems) {
        SaleItemResponse response = new SaleItemResponse();
        response.setSaleItemId(saleItems.getSaleItemsId());
        response.setProduct(productMapper.toResponse(saleItems.getProduct()));
        response.setSaleItemQuantity(saleItems.getSalesItemQuantity());
        response.setSalePrice(saleItems.getSalesPrice());
        Set<VariationOptionsResponse> variationResponses = saleItems.getVariationOptions()
                .stream()
                .map(variationOptionsMapper::toResponse)
                .collect(Collectors.toSet());
        response.setVariationOption(variationResponses);
        return response;
    }
}
