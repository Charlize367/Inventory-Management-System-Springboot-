package org.example.Mapper;

import org.example.DTO.Response.PurchaseItemResponse;
import org.example.DTO.Response.PurchaseResponse;
import org.example.DTO.Response.VariationOptionsResponse;
import org.example.Entities.PurchaseItems;
import org.example.Entities.Purchases;
import org.example.Repository.PurchaseItemsRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class PurchasesMapper {


    private final UsersMapper usersMapper;
    private final SuppliersMapper suppliersMapper;
    private final ProductMapper productMapper;
    private final VariationOptionsMapper variationOptionsMapper;


    private final PurchaseItemsRepository purchaseItemsRepository;

    public PurchasesMapper(PurchaseItemsRepository purchaseItemsRepository, UsersMapper usersMapper, SuppliersMapper suppliersMapper, ProductMapper productMapper, VariationOptionsMapper variationOptionsMapper) {
        this.purchaseItemsRepository = purchaseItemsRepository;
        this.usersMapper = usersMapper;
        this.suppliersMapper = suppliersMapper;
        this.productMapper = productMapper;
        this.variationOptionsMapper = variationOptionsMapper;
    }

    public PurchaseResponse toResponse(Purchases purchase) {
        PurchaseResponse response = new PurchaseResponse();


        List<PurchaseItemResponse> purchaseItems = purchaseItemsRepository.findByPurchase(purchase)
                .stream()
                .map(item -> {
                    PurchaseItemResponse dto = new PurchaseItemResponse();
                    dto.setPurchaseItemId(item.getPurchaseItemsId());
                    dto.setProduct(productMapper.toResponse(item.getProduct()));
                    dto.setPurchaseItemQuantity(item.getPurchaseItemQuantity());
                    dto.setPurchaseCostPrice(item.getPurchaseCostPrice());
                    Set<VariationOptionsResponse> variationResponses = item.getVariationOptions()
                            .stream()
                            .map(variationOptionsMapper::toResponse)
                            .collect(Collectors.toSet());

                    dto.setVariationOptions(variationResponses);
                    return dto;
                })
                .toList();

        response.setPurchaseId(purchase.getPurchaseId());
        response.setStaff(usersMapper.toResponse(purchase.getStaff()));
        response.setSupplier(suppliersMapper.toResponse(purchase.getSupplier()));
        response.setPurchaseAmount(purchase.getPurchaseAmount());
        response.setPurchaseStatus(purchase.getPurchaseStatus().name());
        response.setPurchaseItems(purchaseItems);
        response.setPurchaseDate(purchase.getPurchaseDate());
       return response;
    }
}
