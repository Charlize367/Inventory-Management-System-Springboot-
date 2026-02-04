package org.example.Mapper;

import org.example.DTO.Response.SaleItemResponse;
import org.example.DTO.Response.SalesResponse;
import org.example.DTO.Response.VariationOptionsResponse;
import org.example.Entities.Sales;
import org.example.Repository.SaleItemsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SalesMapper {

    private final UsersMapper usersMapper;
    private final CustomerMapper customerMapper;
    private final SaleItemsRepository saleItemsRepository;
    private final ProductMapper productMapper;
    private final VariationOptionsMapper variationOptionsMapper;

    public SalesMapper(SaleItemsRepository saleItemsRepository, UsersMapper usersMapper, CustomerMapper customerMapper, ProductMapper productMapper, VariationOptionsMapper variationOptionsMapper) {
        this.saleItemsRepository = saleItemsRepository;
        this.usersMapper = usersMapper;
        this.customerMapper = customerMapper;
        this.productMapper = productMapper;
        this.variationOptionsMapper = variationOptionsMapper;
    }

    public SalesResponse toResponse(Sales sales) {

        List<SaleItemResponse> saleItems = saleItemsRepository.findBySales(sales)
                .stream()
                .map(item -> {
                    SaleItemResponse dto = new SaleItemResponse();
                    dto.setSaleItemId(item.getSaleItemsId());
                    dto.setProduct(productMapper.toResponse(item.getProduct()));
                    dto.setSaleItemQuantity(item.getSalesItemQuantity());
                    dto.setSalePrice(item.getSalesPrice());
                    Set<VariationOptionsResponse> variationResponses = item.getVariationOptions()
                            .stream()
                            .map(variationOptionsMapper::toResponse)
                            .collect(Collectors.toSet());

                    dto.setVariationOption(variationResponses);
                    return dto;
                })
                .toList();

        SalesResponse response = new SalesResponse();
        response.setSalesId(sales.getSalesId());
        response.setStaff(usersMapper.toResponse(sales.getStaff()));
        if (sales.getCustomer() != null) {
            response.setCustomer(customerMapper.toResponse(sales.getCustomer()));
        } else {
            response.setCustomer(null);
        }
        response.setSalesAmount(sales.getSalesAmount());
        response.setSalesPaymentStatus(sales.getSalesPaymentStatus().name());
        response.setSalesItem(saleItems);
        response.setSaleDate(sales.getSaleDate());
        return response;
    }
}
