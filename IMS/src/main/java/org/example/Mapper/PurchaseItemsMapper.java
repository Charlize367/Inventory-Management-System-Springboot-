package org.example.Mapper;

import org.example.DTO.Response.PurchaseItemResponse;
import org.example.DTO.Response.VariationOptionsResponse;
import org.example.Entities.PurchaseItems;
import org.example.Repository.PurchaseItemsRepository;
import org.example.Repository.VariationOptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PurchaseItemsMapper {

    private final ProductMapper productMapper;
    private final VariationOptionsMapper variationOptionsMapper;

    @Autowired
    private PurchaseItemsRepository purchaseItemsRepository;



    public PurchaseItemsMapper(ProductMapper productMapper, VariationOptionsMapper variationOptionsMapper) {
        this.productMapper = productMapper;
        this.variationOptionsMapper = variationOptionsMapper;
    }

    public PurchaseItemResponse toResponse(PurchaseItems purchaseItems) {
        PurchaseItemResponse response = new PurchaseItemResponse();
        response.setPurchaseItemId(purchaseItems.getPurchaseItemsId());
        response.setProduct(productMapper.toResponse(purchaseItems.getProduct()));
        response.setPurchaseItemQuantity(purchaseItems.getPurchaseItemQuantity());
        response.setPurchaseCostPrice(purchaseItems.getPurchaseCostPrice());

        Set<VariationOptionsResponse> variationResponses = purchaseItems.getVariationOptions()
                .stream()
                .map(variationOptionsMapper::toResponse)
                .collect(Collectors.toSet());
        response.setVariationOptions(variationResponses);

        System.out.println("Managed item variationOptions: " + purchaseItems.getVariationOptions());

        return response;
    }
}
