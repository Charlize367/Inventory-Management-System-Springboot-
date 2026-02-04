package org.example.Mapper;

import org.example.DTO.Response.PurchaseResponse;
import org.example.DTO.Response.StockMovementResponse;
import org.example.Entities.Products;
import org.example.Entities.Purchases;
import org.example.Entities.StockMovements;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class StockMovementsMapper {

    private final ProductMapper productMapper;
    private final UsersMapper usersMapper;

    public StockMovementsMapper(ProductMapper productMapper, UsersMapper usersMapper) {
        this.productMapper = productMapper;
        this.usersMapper = usersMapper;
    }

    public StockMovementResponse toResponse(StockMovements stockMovements) {
        StockMovementResponse response = new StockMovementResponse();
        response.setStockMovementId(stockMovements.getStockMovementId());
        response.setProduct(productMapper.toResponse(stockMovements.getProduct()));
        response.setQuantityChange(stockMovements.getQuantityChange());
        response.setStockMovement(stockMovements.getStockMovement().name());
        response.setStaff(usersMapper.toResponse(stockMovements.getStaff()));
        response.setCreatedAt(stockMovements.getCreatedAt());
        return response;
    }
}
