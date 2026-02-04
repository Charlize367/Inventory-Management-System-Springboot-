package org.example.Mapper;

import org.example.DTO.Request.ProductRequest;
import org.example.DTO.Response.CategoryResponse;
import org.example.DTO.Response.ProductResponse;
import org.example.Entities.Categories;
import org.example.Entities.Products;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;
    private final VariationMapper variationMapper;
    private final SuppliersMapper suppliersMapper;
    private final BrandMapper brandMapper;

    public ProductMapper(CategoryMapper categoryMapper, VariationMapper variationMapper, SuppliersMapper suppliersMapper, BrandMapper brandMapper) {
        this.categoryMapper = categoryMapper;
        this.variationMapper = variationMapper;
        this.suppliersMapper = suppliersMapper;
        this.brandMapper = brandMapper;
    }
    public Products toEntity(ProductRequest req, Set<Categories> categories) {

        Products product = new Products();
        product.setProductName(req.getProductName());
        product.setProductCost(req.getProductCost());
        product.setProductPrice(req.getProductPrice());
        product.setProductDescription(req.getProductDescription());
        product.setProductStock(req.getProductStock());
        product.setCategories(categories);
        return product;
    }

    public ProductResponse toResponse(Products product) {
        ProductResponse productResponse = new ProductResponse();

        productResponse.setProductId(product.getProductId());
        productResponse.setProductName(product.getProductName());
        productResponse.setProductCost(product.getProductCost());
        productResponse.setProductPrice(product.getProductPrice());
        productResponse.setProductDescription(product.getProductDescription());
        productResponse.setProductStock(product.getProductStock());
        productResponse.setProductImage(product.getProductImage());
        productResponse.setCategories(categoryMapper.toSetResponse(product.getCategories()));
        productResponse.setVariations(variationMapper.toSetResponse(product.getVariations()));
        productResponse.setProductCode(product.getProductCode());
        productResponse.setSupplier(suppliersMapper.toResponse(product.getSupplier()));
        productResponse.setBrand(brandMapper.toResponse(product.getBrand()));

        return productResponse;

    }
}
